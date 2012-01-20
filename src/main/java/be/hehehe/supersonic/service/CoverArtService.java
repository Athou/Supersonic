package be.hehehe.supersonic.service;

import java.io.File;
import java.io.FileInputStream;
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

	public InputStream getCover(String coverId) throws Exception {
		InputStream result = null;

		File imageFile = new File(COVER_PATH + coverId);
		if (imageFile.exists()) {
			result = new FileInputStream(imageFile);
		} else {
			result = subsonicService.invokeBinary("getCoverArt", new Param(
					coverId));
			new File(COVER_PATH).mkdirs();
			IOUtils.copy(result, new FileOutputStream(imageFile));
			result = new FileInputStream(imageFile);
		}
		return result;

	}
}
