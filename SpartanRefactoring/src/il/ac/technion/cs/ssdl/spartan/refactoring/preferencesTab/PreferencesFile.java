package il.ac.technion.cs.ssdl.spartan.refactoring.preferencesTab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Ofir Elmakias <code><elmakias [at] outlook.com></code> (original /
 *         2014/6/21)
 * @author Tomer Zeltzer <code><tomerr90 [at] gmail.com></code> (original /
 *         2014/6/21)
 * @since 2014/6/21
 *
 *        Preferences file handler. Getting file path, phrasing it, etc...
 */
public class PreferencesFile {
	/**
	 * @return Spartanization rules file header
	 */
	public static String[] getSpartanTitle() {
		return new String[] { //
		"Preferences file for Spartanization rules profiles", //
				"Please avoid editing the file manually ", //
		"--------------------------------------------------" };
	}

	/**
	 * @return Spartanization rules preferences file path.
	 */
	public static String getPrefFilePath() {
		final String p = SpartanizationPreferencePage.class
				.getProtectionDomain().getCodeSource().getLocation().getPath();
		final String f = "Sparta.pref";
		return p + f;
	}

	/**
	 * @return Preferences file as lines array
	 */
	public static String[] phrasePrefFile() {
		final String path = getPrefFilePath();
		if (!new File(path).exists())
			return null;
		Scanner sc;
		String[] arr = null;
		try {
			sc = new Scanner(new File(path));
			final List<String> lines = new ArrayList<>();
			while (sc.hasNextLine())
				lines.add(sc.nextLine());
			arr = lines.toArray(new String[0]);
			sc.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return arr;
	}
}