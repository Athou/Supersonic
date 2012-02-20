package be.hehehe.supersonic.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import be.hehehe.supersonic.service.SubsonicService.Param;

@Singleton
public class CoverArtService {

	private static final String COVER_PATH = "cache/covers/";

	@Inject
	SubsonicService subsonicService;

	@Inject
	Logger log;

	public BufferedImage getCoverImage(String coverId) throws IOException {
		return ImageIO.read(getCover(coverId));
	}

	public InputStream getCover(String coverId) {
		if (coverId == null) {
			return getUnknownImage();
		}
		InputStream result = null;
		String coverName = Base64.encodeBase64String(hexToASCII(coverId)
				.getBytes());

		File imageFile = new File(COVER_PATH + coverName);
		if (imageFile.exists()) {
			try {
				result = new FileInputStream(imageFile);
			} catch (FileNotFoundException e) {
				result = getUnknownImage();
			}
		} else {
			try {
				result = subsonicService.invokeBinary("getCoverArt", new Param(
						coverId));
				new File(COVER_PATH).mkdirs();
				IOUtils.copy(result, new FileOutputStream(imageFile));
				result = new FileInputStream(imageFile);
			} catch (Exception e) {
				result = getUnknownImage();
			}
		}
		return result;
	}

	private InputStream getUnknownImage() {
		return getClass().getResourceAsStream("/icons/question-mark.jpg");
	}

	private String hexToASCII(String hex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hex.length() - 1; i += 2) {
			String output = hex.substring(i, (i + 2));
			int decimal = Integer.parseInt(output, 16);
			sb.append((char) decimal);
		}
		return sb.toString();
	}
}
