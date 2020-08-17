package edu.lu.uni.serval.tbar.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.lu.uni.serval.tbar.AbstractFixer;
import edu.lu.uni.serval.tbar.TBarFixer;
import edu.lu.uni.serval.tbar.config.Configuration;

/**
 * Fix bugs with Fault Localization results.
 * 
 * @author kui.liu
 *
 */
public class Main {
	private static String[] apache_commons_lang_224267191 = {
		"/Users/cuong/IdeaProjects/apr-repo/commons-lang",
		"/Users/cuong/IdeaProjects/TBar/SusFiles/PerfectFL/apache_commons_lang_224267191.txt",
		"org.apache.commons.lang3.reflect.MethodUtilsTest#testGetMethodsWithAnnotationSearchSupersAndIgnoreAccess,org.apache.commons.lang3.reflect.MethodUtilsTest#testGetMethodsWithAnnotationSearchSupersButNotIgnoreAccess",
		"/src/main/java/",
		"/src/test/java/",
		"/target/classes/",
		"/target/test-classes/",
	};

	private static String[] Bears_98 = {
		"/Users/cuong/IdeaProjects/apr-repo/traccar",
		"/Users/cuong/IdeaProjects/TBar/SusFiles/PerfectFL/Bears-98.txt",
		"org.traccar.protocol.GoSafeProtocolDecoderTest#testDecode",
		"/src/",
		"/test/",
		"/target/classes/",
		"/target/test-classes/",
	};

	private static String[] Bears_127 = {
			"/Users/cuong/IdeaProjects/apr-repo/traccar",
			"/Users/cuong/IdeaProjects/TBar/SusFiles/PerfectFL/Bears-127.txt",
			"org.traccar.protocol.EelinkProtocolDecoderTest#testDecode",
			"/src/",
			"/test/",
			"/target/classes/",
			"/target/test-classes/",
	};

	public static void main(String[] args) {
		args = Bears_127;

		String projectFolder = args[0];
		String suspiciousFile = args[1];
		String failedTestsStr = args[2];

		Configuration.srcPath = args[3];
		Configuration.testSrcPath = args[4];
		Configuration.classPath = args[5];
		Configuration.testClassPath = args[6];

		Configuration.outputPath += "NormalFL/";
		List<String> failedTests = new ArrayList<>();
		for (String failedTest : failedTestsStr.split(",")) {
			if (!failedTests.contains(failedTest)) {
				failedTests.add(failedTest);
			}
		}

		AbstractFixer fixer = new TBarFixer(projectFolder, failedTests);
		fixer.dataType = "TBar";
		fixer.metric = Configuration.faultLocalizationMetric;
		fixer.suspCodePosFile = new File(suspiciousFile);

		fixer.fixProcess();

		int fixedStatus = fixer.fixedStatus;
		switch (fixedStatus) {
			case 0:
				System.out.println("Failed to fix bug");
				break;
			case 1:
				System.out.println("Succeeded to fix bug");
				break;
			case 2:
				System.out.println("Partial succeeded to fix bug");
				break;
		}
	}

	public static void fixBug(String bugDataPath, String defects4jHome, String bugIdStr) {
		Configuration.outputPath += "NormalFL/";
		String suspiciousFileStr = Configuration.suspPositionsFilePath;
		
		String[] elements = bugIdStr.split("_");
		String projectName = elements[0];
		int bugId;
		try {
			bugId = Integer.valueOf(elements[1]);
		} catch (NumberFormatException e) {
			System.err.println("Please input correct buggy project ID, such as \"Chart_1\".");
			return;
		}
		
		AbstractFixer fixer = new TBarFixer(bugDataPath, projectName, bugId, defects4jHome);
		fixer.dataType = "TBar";
		fixer.metric = Configuration.faultLocalizationMetric;
		fixer.suspCodePosFile = new File(suspiciousFileStr);
		if (Integer.MAX_VALUE == fixer.minErrorTest) {
			System.out.println("Failed to defects4j compile bug " + bugIdStr);
			return;
		}
		
		fixer.fixProcess();
		
		int fixedStatus = fixer.fixedStatus;
		switch (fixedStatus) {
		case 0:
			System.out.println("Failed to fix bug " + bugIdStr);
			break;
		case 1:
			System.out.println("Succeeded to fix bug " + bugIdStr);
			break;
		case 2:
			System.out.println("Partial succeeded to fix bug " + bugIdStr);
			break;
		}
	}

}
