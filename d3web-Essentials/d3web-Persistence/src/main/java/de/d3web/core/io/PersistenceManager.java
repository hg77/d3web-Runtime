/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.d3web.core.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import com.denkbares.plugin.Extension;
import com.denkbares.plugin.Plugin;
import com.denkbares.plugin.PluginManager;
import com.denkbares.progress.CombinedProgressListener;
import com.denkbares.progress.DummyProgressListener;
import com.denkbares.progress.ProgressListener;
import com.denkbares.utils.Log;
import com.denkbares.utils.Streams;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.Resource;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.plugin.Autodetect;
import de.d3web.plugin.PluginConfig;
import de.d3web.plugin.PluginEntry;

/**
 * This class provides the management features to load and save
 * {@link KnowledgeBase} instances to a file system. This manager stores the
 * {@link KnowledgeBase} instance in a compressed file, that contains knowledge
 * base items as XML files together with additional resources.
 * <p>
 * Access this class via the singleton <code>getInstance()</code> method.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public final class PersistenceManager {

	public static final String MULTIMEDIA_PATH_PREFIX = "multimedia/";
	public static final String EXTENDS_PATH_PREFIX = "exports/";

	public static final String EXTENDED_PLUGIN_ID = "KnowledgePersistenceExtensionPoints";
	public static final String EXTENDED_POINT_READER = "KnowledgeReader";
	public static final String EXTENDED_POINT_WRITER = "KnowledgeWriter";
	public static final String EXTENDED_POINT_FRAGMENT = "FragmentHandler";

	private static PersistenceManager instance;

	private Extension[] readerPlugins;
	private Extension[] writerPlugins;
	private final FragmentManager<KnowledgeBase> fragmentManager = new FragmentManager<>();
	private final Cipher cipher;

	public final class KnowledgeBaseInfo {

		private final String name;
		private final String description;
		private final String author;
		private final Date date;
		private final Resource favIcon;
		// format of Date.toString()
		private final SimpleDateFormat format = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

		private KnowledgeBaseInfo(File kbFile, Manifest manifest) {
			this.favIcon = createFavIconResource(kbFile);
			String pureFileName = kbFile.getName().replaceAll("\\.\\p{Alnum}*$", "");
			if (manifest == null) {
				this.name = pureFileName;
				this.description = kbFile.getAbsolutePath();
				this.author = null;
				this.date = new Date(kbFile.lastModified());
			}
			else {
				String manifestName = manifest.getMainAttributes().getValue("Name");
				if (manifestName != null && !manifestName.trim().isEmpty()
						&& !manifestName.equals("null")) {
					this.name = manifestName;
				}
				else {
					this.name = pureFileName;
				}
				this.description = manifest.getMainAttributes().getValue("Description");
				this.author = manifest.getMainAttributes().getValue("Author");
				String dateString = manifest.getMainAttributes().getValue("Date");
				Date parsedDate = null;
				try {
					if (dateString != null) {
						parsedDate = format.parse(dateString);
					}
				}
				catch (ParseException e) {
					// no nothing
				}
				this.date = parsedDate;
			}
		}

		/**
		 * Returns the name of the knowledge base, as specified in the manifest
		 * file.
		 *
		 * @return the name of the knowledge base
		 * @created 17.04.2011
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the description text of the knowledge base, as specified in
		 * the manifest file.
		 *
		 * @return the description of the knowledge base
		 * @created 17.04.2011
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Returns the author's name of this knowledge base, as specified in the
		 * manifest file.
		 *
		 * @return the author of the knowledge base
		 * @created 17.04.2011
		 */
		public String getAuthor() {
			return author;
		}

		/**
		 * Returns the date of this knowledge base, as specified in the manifest
		 * file.
		 *
		 * @return the date of the knowledge base
		 * @created 17.04.2011
		 */
		public Date getDate() {
			return date;
		}

		/**
		 * Returns the fav icon of this knowledge base.
		 *
		 * @return the date of the knowledge base
		 * @created 17.04.2011
		 */
		public Resource getFavIcon() {
			return favIcon;
		}

		private Resource createFavIconResource(File kbFile) {
			try {
				try (ZipFile zipfile = new ZipFile(kbFile)) {
					String path = MULTIMEDIA_PATH_PREFIX + "favicon.png";
					ZipEntry entry = zipfile.getEntry(path);
					if (entry != null) {
						return createResource(kbFile, entry);
					}
				}
			}
			catch (IOException e) {
				// at exception do nothing (providing no icon)
			}
			return null;
		}
	}

	/**
	 * Private constructor: For public access getInstance() should be used
	 */
	private PersistenceManager(Cipher cipher) {
		this.cipher = cipher;
		updatePlugins();
	}

	FragmentManager<KnowledgeBase> getFragmentManager() {
		return fragmentManager;
	}

	/**
	 * Method to force the plugins to be updated. Usually it is not required to
	 * call this method manually, because this will done by the persistence
	 * manager of their own.
	 *
	 * @created 28.11.2013
	 */
	public void updatePlugins() {
		PluginManager manager = PluginManager.getInstance();
		readerPlugins = manager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_READER);
		writerPlugins = manager.getExtensions(EXTENDED_PLUGIN_ID, EXTENDED_POINT_WRITER);
		getFragmentManager().init(EXTENDED_PLUGIN_ID, EXTENDED_POINT_FRAGMENT);
	}

	/**
	 * Method to access the singleton instance of this
	 * {@link PersistenceManager}.
	 *
	 * @return the instance of this {@link PersistenceManager}
	 */
	public static PersistenceManager getInstance() {
		if (instance == null) {
			instance = new PersistenceManager(null);
		}
		return instance;
	}

	/**
	 * Method to create a instance of a {@link PersistenceManager} that
	 * reads/writes encrypted knowledge base files. The encryption of each
	 * content item is done by the specified cipher. The cipher need to be fully
	 * initialized. The entry names of the knowledge base jar file are not
	 * encrypted at all.
	 * <p>
	 * When loading a knowledge base, the Cipher also must not been modified or
	 * reinitialized during the lifetime of the knowledge base, because the
	 * persistence manager may be used later on to reload/encrypt some
	 * additional data as they are required, e.g. multimedia content stored in
	 * the knowledge base file.
	 * <p>
	 * Please not that the same {@link PersistenceManager} usually cannot be
	 * used used for read and write, because the specified cipher is either
	 * initialized for encryption or decryption mode.
	 *
	 * @return the instance of this encrypting/encrypting {@link PersistenceManager}
	 */
	public static PersistenceManager getInstance(Cipher cipher) {
		return new PersistenceManager(cipher);
	}

	/**
	 * Loads a knowledge base from a specified ZIP file and notifies the
	 * specified listener about the working progress.
	 *
	 * @param file the specified ZIP {@link File} (usually a jar file)
	 * @param listener the specified listener which should be notified about the load progress
	 * @return a {@link KnowledgeBase} instance with the knowledge contained in the specified ZIP
	 * file
	 * @throws IOException if an error occurs during opening and reading the file
	 */
	public KnowledgeBase load(File file, ProgressListener listener) throws IOException {
		try (ZipFile zipfile = new ZipFile(file)) {
			KnowledgeBase kb = new KnowledgeBase();
			List<ZipEntry> files = new LinkedList<>();
			// pre-calculate the size of the files to be parsed
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			long size = 0;
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				// ignore directories
				if (entry.isDirectory()) continue;
				// ignore unrequired files necessary for previous versions
				// of persistence
				if (notNeeded(entry)) continue;
				// tread multimedia as size 1,
				// because the are only registered and unpacked on demand
				size += isMultimediaEntry(entry) ? 1 : entry.getSize();
				files.add(entry);
			}
			CombinedProgressListener cpl = new CombinedProgressListener(size, listener);
			boolean parsedAnyFile = false;
			for (Extension plugin : readerPlugins) {
				for (ZipEntry entry : new LinkedList<>(files)) {
					String name = entry.getName();
					// checks if this entry can be parsed with this plugin
					boolean canparse = false;
					String filepattern = plugin.getParameter("filepattern");
					String filename = plugin.getParameter("filename");
					if (filepattern != null) {
						if (name.matches(filepattern)) {
							canparse = true;
						}
					}
					if (filename != null) {
						if (name.equals(filename)) {
							canparse = true;
						}
					}
					if (canparse) {
						// if we can parse this entry
						// we prepare the progress for the next step
						cpl.next(entry.getSize());
						cpl.updateProgress(0, "reading entry " + name);
						// initialize the reader
						KnowledgeReader reader = (KnowledgeReader) plugin.getNewInstance();
						reader.read(this, kb, createInputStream(zipfile, entry), cpl);
						// and mark this as done
						files.remove(entry);
						parsedAnyFile = true;
					}
				}
			}

			// if we not have parsed at least one file
			// we assume that this is no valid knowledge base
			if (!parsedAnyFile) {
				throw new IOException("The parsed file appears not to be a valid knowledge base");
			}

			// finally scan all files in multimedia folder
			// and add them to the knowledge base as resources
			for (ZipEntry entry : files) {
				String name = entry.getName();
				if (isMultimediaEntry(entry)) {
					// prepare progress for the next step,
					// being "1" for multimedia
					cpl.next(1);
					cpl.updateProgress(0, "reading file " + name);
					kb.addResouce(createResource(file, entry));
					// we prepare the progress for the next step
				}
				else {
					cpl.next(entry.getSize());
					cpl.updateProgress(0, "reading file " + name);
					if (!name.startsWith(EXTENDS_PATH_PREFIX)) {
						Log.warning("No parser for entry " + name +
								" available. This file will be lost" +
								" when saving the KnowledgeBase.");
					}
				}
			}
			// knowledge base loaded successfully
			listener.updateProgress(1, "knowledge base loaded successfully");
			return kb;
		}
	}

	/**
	 * Creates an input stream for a specified zip entry to handle decryption.
	 * If there is no request for decryption, the original zip input stream for
	 * that entry is returned.
	 *
	 * @param zipfile the zip file to read from
	 * @param entry the entry to be read
	 * @return the decrypted stream
	 * @throws IOException if there is any io issue
	 * @created 27.11.2013
	 */
	private InputStream createInputStream(ZipFile zipfile, ZipEntry entry) throws IOException {
		InputStream stream = zipfile.getInputStream(entry);
		if (cipher == null) return stream;
		return new CipherInputStream(stream, cipher);
	}

	/**
	 * Creates a decrypted resource for the specified zip entry. If there is no
	 * request for decryption, the original zip input stream for that entry is
	 * returned.
	 *
	 * @param file the zip file to read from
	 * @param entry the entry to be read
	 * @return the decrypted resource
	 * @throws IOException if there is any io issue
	 * @created 27.11.2013
	 */
	private JarBinaryResource createResource(File file, ZipEntry entry) throws IOException {
		return new JarBinaryResource(entry, file, cipher);
	}

	private boolean isMultimediaEntry(ZipEntry entry) {
		return entry.getName().toLowerCase().startsWith(MULTIMEDIA_PATH_PREFIX);
	}

	/**
	 * Returns the the specified entry is an unrequired file from a previous
	 * version of the persistence, that cannot be read any longer.
	 *
	 * @param entry the entry to be checked
	 * @return if the entry should not be parsed
	 * @created 18.06.2011
	 */
	private boolean notNeeded(ZipEntry entry) {
		String name = entry.getName();
		return name.equalsIgnoreCase("KB-INF/Index.xml")
				|| name.equalsIgnoreCase("CRS-INF/Index.xml")
				|| name.equals("META-INF/MANIFEST.MF");
	}

	/**
	 * Loads a knowledge base from the specified ZIP file.
	 *
	 * @param file the specified ZIP {@link File} (usually a jar file)
	 * @return a {@link KnowledgeBase} instance with the knowledge contained in the specified ZIP
	 * file
	 * @throws IOException if an error occurs during opening and reading the file
	 */
	public KnowledgeBase load(File file) throws IOException {
		return load(file, new DummyProgressListener());
	}

	public KnowledgeBaseInfo loadKnowledgeBaseInfo(File file) throws IOException {
		try (JarFile jarfile = new JarFile(file)) {
			Manifest manifest = jarfile.getManifest();
			return new KnowledgeBaseInfo(file, manifest);
		}
	}

	/**
	 * Saves the knowledge base to the specified {@link File}. The file is a
	 * compressed ZIP containing different XML files and resources comprising
	 * the knowledge base.
	 *
	 * @param knowledgeBase the specified knowledge base to be saved
	 * @param file the specified file to which the knowledge base should be stored
	 * @param listener listener which should be informed about the progress of the save operation
	 * @throws IOException if an error occurs during saving the files
	 */
	public void save(KnowledgeBase knowledgeBase, File file, ProgressListener listener) throws IOException {
		Manifest manifest = new Manifest();
		Attributes mainAttributes = manifest.getMainAttributes();
		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "2.0");
		mainAttributes.put(new Attributes.Name("Date"), new Date().toString());
		String prompt = knowledgeBase.getInfoStore().getValue(MMInfo.PROMPT);
		mainAttributes.put(new Attributes.Name("Name"),
				prompt != null ? prompt : knowledgeBase.getName());
		mainAttributes.put(new Attributes.Name("ID"), knowledgeBase.getId());
		mainAttributes.put(new Attributes.Name("Author"),
				knowledgeBase.getInfoStore().getValue(BasicProperties.AUTHOR));
		mainAttributes.put(new Attributes.Name("User"), System.getProperty("user.name"));
		if (cipher != null) {
			mainAttributes.put(new Attributes.Name("Encrypted"), cipher.getAlgorithm());
		}
		File tempfile = new File(file.getCanonicalPath() + ".temp");

		tempfile.getAbsoluteFile().getParentFile().mkdirs();

		// update plugin configuration
		PluginConfig pluginConfig = getUpdatedPluginConfig(knowledgeBase);
		List<WriterInfo> preparedWriters = Arrays.stream(writerPlugins)
				.map(plugin -> new WriterInfo(pluginConfig, plugin))
				.collect(Collectors.toList());
		int size = preparedWriters.stream().mapToInt(WriterInfo::getEstimatedSize).sum()
				+ knowledgeBase.getResources().size();
		CombinedProgressListener cpl = new CombinedProgressListener(size, listener);
		try (JarOutputStream jarOutputStream = new JarOutputStream(
				new FileOutputStream(tempfile), manifest)) {

			for (WriterInfo writerInfo : preparedWriters) {
				// if autodetect is available, the file is only written when the
				// autodetect check is positive
				if (!writerInfo.isRequired()) {
					continue;
				}
				jarOutputStream.putNextEntry(writerInfo.createZipEntry());

				cpl.next(writerInfo.getEstimatedSize());
				// unfortunately we corrupt the jar stream if we directly
				// connect the cipher stream to the entry stream
				if (cipher == null) {
					writerInfo.getWriter().write(this, knowledgeBase, jarOutputStream, cpl);
				}
				else {
					// therefore if encrypted, write to encrypted byte[] first
					// afterwards write the bytes to the jar stream.
					ByteArrayOutputStream bytes = new ByteArrayOutputStream(256 * 1024);
					CipherOutputStream stream = new CipherOutputStream(bytes, cipher);
					writerInfo.getWriter().write(this, knowledgeBase, stream, cpl);
					stream.close();
					bytes.writeTo(jarOutputStream);
				}
			}
			cpl.next(knowledgeBase.getResources().size());
			int i = 0;
			for (Resource resource : knowledgeBase.getResources()) {
				ZipEntry entry = new ZipEntry(MULTIMEDIA_PATH_PREFIX + resource.getPathName());
				jarOutputStream.putNextEntry(entry);
				try (InputStream inputStream = resource.getInputStream()) {
					// unfortunately we corrupt the jar stream if we directly
					// connect the cipher stream to the entry stream
					if (cipher == null) {
						Streams.stream(inputStream, jarOutputStream);
					}
					else {
						// therefore if encrypted, write to encrypted byte[]
						// afterwards write the bytes to the jar stream.
						ByteArrayOutputStream bytes = new ByteArrayOutputStream(
								(int) resource.getSize() + 1);
						CipherOutputStream stream = new CipherOutputStream(bytes, cipher);
						Streams.stream(inputStream, stream);
						stream.close();
						bytes.writeTo(jarOutputStream);
					}
				}
				i++;
				float percent = i / (float) knowledgeBase.getResources().size();
				cpl.updateProgress(percent, "Saving binary resources");
			}
		}
		File bakfile = new File(URLDecoder.decode(file.getCanonicalPath() + ".bak", "UTF-8"));
		// delete old backup file
		bakfile.delete();
		// backup original file, if it exists
		if (file.exists() && !file.renameTo(bakfile)) throw new IOException(
				"Cannot override existing knowledge base file");
		// override original file
		if (!tempfile.renameTo(file)) {
			// if not successful, restore backup and delete created output file
			if (bakfile.exists()) bakfile.renameTo(file);
			tempfile.delete();
			throw new IOException("Cannot rename temporary file");
		}
		// if successful backup is not needed any more
		bakfile.delete();
	}

	private PluginConfig getUpdatedPluginConfig(KnowledgeBase knowledgeBase) {
		PluginConfig pc = PluginConfig.getPluginConfig(knowledgeBase);
		for (Plugin plugin : PluginManager.getInstance().getPlugins()) {
			PluginEntry pluginEntry = pc.getPluginEntry(plugin.getPluginID());
			// when there is no entry, create one with auto-detect = true
			if (pluginEntry == null) {
				pluginEntry = new PluginEntry(plugin);
				pc.addEntry(pluginEntry);
			}
			// when autodetect is true, refresh the necessary state
			if (pluginEntry.isAutodetect()) {
				Autodetect auto = pluginEntry.getAutodetect();
				// should not be null, because the pluginEntry return
				// isAutodetect only if the plugin has autodetection
				// capabilities
				if (auto != null) {
					pluginEntry.setRequired(auto.check(knowledgeBase));
				}
			}
		}
		return pc;
	}

	/**
	 * Saves the specified {@link KnowledgeBase} instance to the specified
	 * {@link File}. During this process, a temporary file is created. If the
	 * process is successful, the temporary file replaces the input file.
	 *
	 * @param knowledgeBase the specified {@link KnowledgeBase} instance to be saved to the file
	 * @param file the specified {@link File} in which the knowledge base is written
	 * @throws IOException if an error occurs, an IO Exception is thrown
	 */
	public void save(KnowledgeBase knowledgeBase, File file) throws IOException {
		save(knowledgeBase, file, new DummyProgressListener());
	}

	private static class WriterInfo {

		private final PluginConfig pluginConfig;
		private final Extension plugin;
		private final KnowledgeWriter writer;
		private final int estimatedSize;

		public WriterInfo(PluginConfig pluginConfig, Extension plugin) {
			this.pluginConfig = pluginConfig;
			this.plugin = plugin;
			this.writer = (KnowledgeWriter) plugin.getNewInstance();
			this.estimatedSize = writer.getEstimatedSize(pluginConfig.getKnowledgeBase());
		}

		public Extension getPlugin() {
			return plugin;
		}

		public KnowledgeWriter getWriter() {
			return writer;
		}

		public int getEstimatedSize() {
			return estimatedSize;
		}

		public boolean isRequired() {
			Autodetect autodetect =
					pluginConfig.getPluginEntry(plugin.getPluginID()).getAutodetect();
			return (autodetect == null) || autodetect.check(pluginConfig.getKnowledgeBase());
		}

		public ZipEntry createZipEntry() throws IOException {
			String filename = plugin.getParameter("filename");
			if (filename == null) {
				throw new IOException("No filename defined in plugin.xml");
			}
			return new ZipEntry(filename);
		}
	}
}
