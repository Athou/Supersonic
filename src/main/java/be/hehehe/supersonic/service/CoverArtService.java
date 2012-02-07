package be.hehehe.supersonic.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;

import be.hehehe.supersonic.service.SubsonicService.Param;

@Singleton
public class CoverArtService {

	private static final String COVER_PATH = "cache/covers/";

	@Inject
	SubsonicService subsonicService;

	public InputStream getCover(String coverId) {
		InputStream result = null;

		File imageFile = new File(COVER_PATH + coverId);
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
