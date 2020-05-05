package org.jis.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Random;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

public class LayoutGalerieTest {
	
	private LayoutGalerie galerieUnderTest;
	
	private File fromFile;
	private File toFile; 
	
	/**
	 * Set up the tests
	 */
	@Before
	public final void prepareGalerieTests() {
		galerieUnderTest = new LayoutGalerie(null, null);
	}
		
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)}.
	 */
	@Test
	public final void testCopyFile() throws URISyntaxException {
		try {
			final File resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			toFile = new File(resourceFolder, "to");
			
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);
		 			 
			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, randomString);
			 
			galerieUnderTest.copyFile(fromFile, toFile);
			 
			assertTrue(toFile.exists());
			 
			Path toPath = FileSystems.getDefault().getPath(toFile.getPath());
			String contents = Files.readString(toPath);
			 		 
			assertEquals(randomString, contents);
		 }
		 catch (IOException | URISyntaxException e) {
			fail();
		 }
		
	}
	
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)} while trying to copy a folder.
	 */
	@Test(expected = FileNotFoundException.class)
	public final void testCopyFolder() throws FileNotFoundException {		
		try {
			final File resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			assertTrue("Could not create directory", fromFile.mkdir()); //TODO: Rework
//			fromFile.mkdir();
			toFile = new File(resourceFolder, "to");
			galerieUnderTest.copyFile(fromFile, toFile);
			}
		catch (FileNotFoundException e) {
			throw e;
			}
		catch (IOException | URISyntaxException e) {
			fail();
			}
	}
	
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)} while trying to copy a non-existing file.
	 */
	@Test(expected = FileNotFoundException.class)
	public final void testCopyNonExisting() throws FileNotFoundException {		
		try {
			final File resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			toFile = new File(resourceFolder, "to");
			galerieUnderTest.copyFile(fromFile, toFile);
			}
		catch (FileNotFoundException e) {
			throw e;
			}
		catch (IOException | URISyntaxException e) {
			fail();
			}
	}
	
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)} while trying to copy an already existing file.
	 */
	@Test
	public final void testCopyAlreadyExisting() {
		try {
			final File resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			toFile = new File(resourceFolder, "to");
			
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);
		 			 
			fromFile.createNewFile();
			toFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Path toPath = FileSystems.getDefault().getPath(toFile.getPath());
			Files.writeString(fromPath, randomString);
			Files.writeString(toPath, "placeholder to override");
			 
			galerieUnderTest.copyFile(fromFile, toFile);
			 
			assertTrue(toFile.exists());

			String contents = Files.readString(toPath);
			 		 
			assertEquals(randomString, contents);
		 }
		 catch (IOException | URISyntaxException e) {
			fail();
		 }
		
	}
	
	/**
	 * Test method for {@link org.jis.generator.LayoutGalerie#copyFile(File, File)} while trying to copy from a file without read access.
	 */
	@Test(expected = IOException.class)
	public final void testCopyFileNoReadAccess() throws IOException{
		FileLock lock;
		FileChannel fChannel;
		try {
			final File resourceFolder = new File(this.getClass().getResource(File.separator).toURI());
			fromFile = new File(resourceFolder, "from");
			toFile = new File(resourceFolder, "to");
			
			byte[] array = new byte[10];
			new Random().nextBytes(array);
			String randomString = new String(array);
		 			 
			fromFile.createNewFile();
			Path fromPath = FileSystems.getDefault().getPath(fromFile.getPath());
			Files.writeString(fromPath, randomString);
			
			fChannel = FileChannel.open(fromPath, StandardOpenOption.WRITE);
			lock = fChannel.lock(0L, Long.MAX_VALUE, false);

			try {
				galerieUnderTest.copyFile(fromFile, toFile);
			}
			catch (IOException e) {
				lock.release();
				fChannel.close();
				throw e;
			}
			finally {
				lock.release();
				fChannel.close();
			}

		 }
		 catch (URISyntaxException e) {
			fail();
		 }
	}
	
	/**
	 * Remove used Files
	 */
	@After
	public final void removeCopiedFile() {
		galerieUnderTest = null;
		if (fromFile.exists()) {
			assertTrue(fromFile.delete());
		}
		if (toFile.exists()) {
			assertTrue(toFile.delete());
		}
	}

}
