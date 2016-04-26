package org.jmc.export;

import org.jmc.util.BlockCorrespondance;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.jmc.util.Resources.load;
import static org.jmc.util.Resources.loadCustom;

/**
 * Created by Paul on 25/04/2016.
 */
public class TextureExporter {
	private static final String TEX_FOLDER = "conf/textures/tex";
	private static final int TEXTURE_SIZE = 128;

	/**
	 * If path is null, load default texture
	 * if path isn't null, try to load custom texture
 	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Iterator<Map.Entry<String, InputStream>> loadTextures(String path) throws IOException {
		Iterator<Map.Entry<String, InputStream>> textures;
		if (path != null && path.length() > 0) {
			textures = loadCustomTextures(path);
		} else {
			textures = loadDefaultTextures();
		}
		return textures;
	}


	private static Iterator<Map.Entry<String, InputStream>> loadCustomTextures(String path) throws IOException {
		Map<String, String> textures = loadTextureFromJar();

		File file = Paths.get(path).toFile();
		if (!isTextureRepertory(file)) {
			throw new IOException("This is not a Texture File !");
		}

		//Get Blocks
		Path completPath = Paths.get(path, "assets", "minecraft", "textures");
		Map<String, String> texturesCustom = listAllTexture(completPath.toString());

		File customTexturesRepertory = Paths.get(System.getProperty("user.dir"), "customTextures").toFile();
		customTexturesRepertory.mkdir();
		String customPath;

		/* Replace default image */
		for (String name : textures.keySet()) {
			//Look if we stock the block info in BlockCorrespondance
			BlockCorrespondance.Block block = BlockCorrespondance.get(name);
			if (block == null) {
				String p = texturesCustom.get(name);
				if(p != null) {
					//We didn't get any information for the current block, but we got a custom texture in the repertory

					//Square if needed
					customPath = squareImage(p, name);

					//Resize if needed image
					customPath = resizeImage(name, customPath, TEXTURE_SIZE);
					textures.put(name, customPath);
				}
			} else {
				customPath = texturesCustom.get(block.mtlName);
				if (customPath != null){
					//We got some informations about the block
					if (block.isSquare) {
						customPath = squareImage(customPath, name);
					}

					if (!block.tint.equals("")){
						//This block need a particular tint
						customPath = tintImage(customPath, name, new Color(Integer.parseInt(block.tint, 16)));
					}

					//Resize image
					customPath = resizeImage(name, customPath, TEXTURE_SIZE);
					textures.put(name, customPath);
				} else {
					//TODO: Correct message here
					System.out.println(name + " texture is missing.");
				}
			}
		}

		final Iterator<Map.Entry<String, String>> iterator = textures.entrySet().iterator();

		return new Iterator<Map.Entry<String, InputStream>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Map.Entry<String, InputStream> next() {
				Map.Entry<String, String> entry = iterator.next();
				return new AbstractMap.SimpleEntry<>(entry.getKey(), loadCustom(entry.getValue()));
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}



	private static Iterator<Map.Entry<String, InputStream>> loadDefaultTextures() throws IOException {
		// load texture paths by name
		final Map<String, String> textures = loadTextureFromJar();
		final Iterator<Map.Entry<String, String>> iterator = textures.entrySet().iterator();

		return new Iterator<Map.Entry<String, InputStream>>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public Map.Entry<String, InputStream> next() {
				Map.Entry<String, String> entry = iterator.next();
				return new AbstractMap.SimpleEntry<>(entry.getKey(), load(entry.getValue()));
			}

			@Override
			public void remove() {
				iterator.remove();
			}
		};
	}

	private static Map<String, String> loadTextureFromJar() throws IOException {
		final Map<String, String> textures = new HashMap<>();
		final File jarFile = new File(TextureExporter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		if (jarFile.isFile()) {
			final JarFile jar = new JarFile(jarFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements()) {
				String test = entries.nextElement().getName();
				if (test.startsWith(TEX_FOLDER) && !test.equals(TEX_FOLDER + "/")) { //filter according to the path
					String[] segments = test.split("/");
					textures.put(segments[segments.length - 1], "/" + test);
				}
			}
			jar.close();
		} else {
			BufferedReader in=new BufferedReader(new InputStreamReader(load("/" + TEX_FOLDER)));
			String texName;
			while((texName=in.readLine()) != null) {
				textures.put(texName, "/" + TEX_FOLDER + "/" + texName);
			}
		}
		return textures;
	}

	private static Map<String, String> listAllTexture(String path){
		Map<String, String> textures= new HashMap<>();

		File repertory = new File(path);
		if (!repertory.exists()) {
			return null;
		}
		for (File file : repertory.listFiles()){
			if (!file.isDirectory()) {
				if (file.getName().contains(".png")) {
					textures.put(file.getName(), file.getAbsolutePath());
				}
			} else {
				if ((file.getPath().contains("blocks") || file.getPath().contains("entity") || file.getPath().contains("models") || file.getPath().contains("particle")) && !file.getPath().contains("slime")) {
					Map<String, String> subtextures = listAllTexture(file.getAbsolutePath());
					if (subtextures != null) {
						textures.putAll(subtextures);
					}

				}
			}
		}
		return textures;
	}

	public static boolean isTextureRepertory(File file) {
		File[] subfiles = file.listFiles();
		boolean gotAssets = false;
		boolean gotMcmeta = false;
		boolean gotMinecraft = false;

		if (!file.exists() || !file.isDirectory()) {
			return false;
		}

		for (int i = 0; i < subfiles.length; i++) {
			if (subfiles[i].getName().equals("assets")) {
				gotAssets = true;
				File[] assetsFiles = subfiles[i].listFiles();
				for (int j = 0; j < assetsFiles.length; j++) {
					if (assetsFiles[j].getName().equals("minecraft")) {
						gotMinecraft = true;
					}
				}
			} else if (subfiles[i].getName().equals("pack.mcmeta")) {
				gotMcmeta = true;
			}

			if( gotAssets && gotMcmeta && gotMinecraft ) {
				return true;
			}
		}
		return false;
	}

	private static String squareImage(String path, String name) {
		//Entities aren't square !
		if (path.contains("entity")){
			return path;
		}

		File customTexturesRepertory = Paths.get(System.getProperty("user.dir"), "customTextures").toFile();
		try {
			BufferedImage image = ImageIO.read(new File(path));
			int size;

			if (image.getHeight() == image.getWidth()) {
				return path;
			}else if (image.getWidth() > image.getHeight()){
				size = image.getHeight();
			} else {
				size = image.getWidth();
			}
			BufferedImage newImage = new BufferedImage(size,
					size,
					BufferedImage.TYPE_INT_ARGB);

			newImage.getGraphics().drawImage(image, 0, 0, null);
			ImageIO.write(newImage, "png", new File(customTexturesRepertory.getAbsolutePath(), name ));
			return customTexturesRepertory.getAbsolutePath() + "/" + name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	private static String resizeImage(String name, String path, int size) {
		BufferedImage img;
		File customTexturesRepertory = Paths.get(System.getProperty("user.dir"), "customTextures").toFile();

		try {
			img = ImageIO.read(new File(path));
			Image thumbnail;
			if (img.getWidth() == size || img.getHeight() == size){
				return path;
			} else if (img.getWidth() > img.getHeight()){
				thumbnail = img.getScaledInstance(size, -1, Image.SCALE_SMOOTH);
			} else {
				thumbnail = img.getScaledInstance(-1, size, Image.SCALE_SMOOTH);
			}

			BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),
					thumbnail.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);

			bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
			ImageIO.write(bufferedThumbnail, "png", new File(customTexturesRepertory.getAbsolutePath(), name ));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return customTexturesRepertory.getAbsolutePath() + "/" + name;
	}
	private static String tintImage(String path, String name, Color tint) {
		BufferedImage img;
		File customTexturesRepertory = Paths.get(System.getProperty("user.dir"), "customTextures").toFile();
		try {
			img = ImageIO.read(new File(path));

			int w = img.getWidth();
			int h = img.getHeight();
			int c = img.getColorModel().getPixelSize() / 8;

			if (c != 4) {
				throw new ImagingOpException("Texture is not 32-bit!");
			}

			int[] buffer = new int[w * h * c];

			WritableRaster raster = img.getRaster();
			raster.getPixels(0, 0, w, h, buffer);

			int r = tint.getRed();
			int g = tint.getGreen();
			int b = tint.getBlue();

			for (int i = 0; i < w * h; i++) {
				c = (buffer[4 * i] * r) >> 8;
				if (c > 255)
					c = 255;
				buffer[4 * i] = c;

				c = (buffer[4 * i + 1] * g) >> 8;
				if (c > 255)
					c = 255;
				buffer[4 * i + 1] = c;

				c = (buffer[4 * i + 2] * b) >> 8;
				if (c > 255)
					c = 255;
				buffer[4 * i + 2] = c;
			}

			raster.setPixels(0, 0, w, h, buffer);
			ImageIO.write(img, "png", new File(customTexturesRepertory.getAbsolutePath(), name ));

			return customTexturesRepertory.getAbsolutePath()+"/"+name;
		} catch (IOException e) {
			return path;
		}
	}
}
