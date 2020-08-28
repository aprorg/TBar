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

	public static void main(String[] args) {
		// traccar
//		args = Bugs.tananaev_traccar_82839755;
//		args = Bugs.Bears_127;
//		args = Bugs.Bears_102;
//		args = Bugs.tananaev_traccar_64783123;
//		args = Bugs.Bears_98;
//		args = Bugs.tananaev_traccar_68883949;
//		args = Bugs.Bears_121;
//		args = Bugs.Bears_139;
//		args = Bugs.tananaev_traccar_221926468;
//		args = Bugs.Bears_116;
//		args = Bugs.Bears_110;

//		args = Bugs.apache_commons_lang_224267191;

		// spoon
//		args = Bugs.Bears_217;
//		args = Bugs.Bears_78;
//		args = Bugs.Bears_42;
//		args = Bugs.Bears_56;
//		args = Bugs.Bears_74;

//		args = Bugs.sannies_mp4parser_79111320;
		args = Bugs.stagemonitor_stagemonitor_145477129;

		String projectFolder = args[0];
		String suspiciousFile = args[1];
		String failedTestsStr = args[2];

		Configuration.srcPath = args[3];
		Configuration.testSrcPath = args[4];
		Configuration.classPath = args[5];
		Configuration.testClassPath = args[6];

		String ignoredTestsStr = args.length > 7 ? args[7] : "";

		Configuration.outputPath += "NormalFL/";
		List<String> ignoredTests = new ArrayList<>();
		List<String> failedTests = new ArrayList<>();

		if (!ignoredTestsStr.isEmpty()) {
			for (String ignoredTest : ignoredTestsStr.split(",")) {
				if (!ignoredTests.contains(ignoredTest)) {
					ignoredTests.add(ignoredTest);
				}
			}
		}

		for (String failedTest : failedTestsStr.split(",")) {
			if (!failedTests.contains(failedTest) && !ignoredTests.contains(failedTest)) {
				failedTests.add(failedTest);
			}
		}

		String bugId = new File(suspiciousFile).getName().replace(".txt", "");
		AbstractFixer fixer = new TBarFixer(projectFolder, failedTests, ignoredTests, bugId);
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
