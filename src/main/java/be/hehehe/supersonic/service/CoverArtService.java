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
		BufferedImage image = null;
		InputStream is = null;
		try {
			is = getCover(coverId);
			image = ImageIO.read(is);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return image;
	}

	@SuppressWarnings("resource")
	private InputStream getCover(String coverId) {
		if (coverId == null) {
			return getUnknownImage();
		}
		InputStream result = null;

		File imageFile = new File(COVER_PATH + coverId + ".jpg");
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
}
