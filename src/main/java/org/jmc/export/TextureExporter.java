package org.jmc.export;

import org.jmc.util.BlocksMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
	private final String TEX_FOLDER = "conf/textures/tex";
	private final int TEXTURE_SIZE = 256;
	public  final String PATH_TO_CUSTOM_TEXTURE = Paths.get(System.getProperty("user.dir"), "customTextures").toAbsolutePath().toString();

	private final String texturePath;
	private final ProgressCallback progress;

	public TextureExporter(String texturePath, ProgressCallback progress) {
		this.texturePath = texturePath;
		this.progress = progress;
	}

	public Iterator<Map.Entry<String, InputStream>> loadTextures() throws IOException {
		Map<String, String> textures;
		if (texturePath != null && texturePath.length() > 0) {
			textures = loadCustomTextures(texturePath);
		} else {
			textures = loadTextureFromJar();
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


	private Map<String, String> loadCustomTextures(String path) throws IOException {
		Map<String, String> textures = loadTextureFromJar();

		//Get textures
		Path fullPath = Paths.get(path, "assets", "minecraft", "textures");
		Map<String, String> texturesCustom = listAllTexture(fullPath.toString());

		File customTexturesRepertory = Paths.get(PATH_TO_CUSTOM_TEXTURE).toFile();
		customTexturesRepertory.mkdir();

		int cpt = 0;

		/** Replace default image **/
		for (String name : new ArrayList<>(textures.keySet())) {
			BlocksMap.Block block = BlocksMap.get(name);
			if (block == null) {
				if (texturesCustom.get(name) != null) {
					transformTexture(new BlocksMap.Block(name, name, "", true), textures, texturesCustom.get(name), false);
				}
			} else {
				transformTexture(block, textures, texturesCustom.get(block.mtlName), true);
			}

			//Update progress bar
			if (progress != null) {
				float progValue = (float) cpt / (float) textures.size();
				progress.setProgress(progValue);
				cpt++;
			}
		}
		return textures;
	}

	private void transformTexture(BlocksMap.Block block, Map<String, String> textures, String previousPath, boolean alreadyInMap){
		String path = previousPath;
		if (path != null){
			if (block.isSquare) {
				path = squareImage(path, block.fileName);
			}
			if (!block.tint.equals("")){
				path = tintImage(path, block.fileName, new Color(Integer.parseInt(block.tint, 16)));
			}
			path = resizeImage(block.fileName, path);
			textures.remove(block.fileName);
			textures.put(block.fileName, path);
		} else if(alreadyInMap) {
			System.out.println(block.fileName + " texture is missing.");
		}
	}


	private Map<String, String> loadTextureFromJar() throws IOException {
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

	private Map<String, String> listAllTexture(String path){
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

	private String squareImage(String path, String name) {
		//Entities aren't square !
		if (path.contains("entity")){
			return path;
		}

		try {
			BufferedImage src = ImageIO.read(new File(path));
			int size;

			if (src.getHeight() == src.getWidth()) {
				return path;
			}else if (src.getWidth() > src.getHeight()){
				size = src.getHeight();
			} else {
				size = src.getWidth();
			}
			BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

			resized.getGraphics().drawImage(src, 0, 0, null);
			ImageIO.write(resized, "png", new File(PATH_TO_CUSTOM_TEXTURE, name ));
			return PATH_TO_CUSTOM_TEXTURE + "/" + name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
	private String resizeImage(String name, String path) {
		try {
			BufferedImage src = ImageIO.read(new File(path));

			int newWidth;
			int newHeight;
			if (src.getWidth() == TEXTURE_SIZE || src.getHeight() == TEXTURE_SIZE){
				return path;
			}
			int minSize = src.getWidth() > src.getHeight() ? src.getHeight() : src.getWidth();
			float scale = TEXTURE_SIZE / (float) minSize;

			newWidth = (int) (scale * src.getWidth());
			newHeight = (int) (scale * src.getHeight());

			BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = resized.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(src, 0, 0, newWidth, newHeight, null);
			g2.dispose();

			ImageIO.write(resized, "png", new File(PATH_TO_CUSTOM_TEXTURE, name));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return PATH_TO_CUSTOM_TEXTURE + "/" + name;
	}
	private String tintImage(String path, String name, Color tint) {
		try {
			BufferedImage src = ImageIO.read(new File(path));

			int w = src.getWidth();
			int h = src.getHeight();
			int c = src.getColorModel().getPixelSize() / 8;
			int nbCanal = c;

			int[] buffer = new int[w * h * nbCanal];

			WritableRaster raster = src.getRaster();
			raster.getPixels(0, 0, w, h, buffer);

			int r = tint.getRed();
			int g = tint.getGreen();
			int b = tint.getBlue();

			for (int i = 0; i < w * h; i++) {
				c = (buffer[nbCanal * i] * r) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i] = c;

				c = (buffer[nbCanal * i + 1] * g) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i + 1] = c;

				c = (buffer[nbCanal * i + 2] * b) >> 8;
				if (c > 255)
					c = 255;
				buffer[nbCanal * i + 2] = c;
			}

			raster.setPixels(0, 0, w, h, buffer);
			ImageIO.write(src, "png", new File(PATH_TO_CUSTOM_TEXTURE, name ));

			return PATH_TO_CUSTOM_TEXTURE + "/" + name;
		} catch (IOException e) {
			return path;
		}
	}
}
