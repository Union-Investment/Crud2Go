package de.uit.eai.portal.mvn.plugin;

import de.uit.eai.portal.mvn.plugin.SourceTransformer.RESULT;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

public class SourceTransformerTest {

	@Test
	public void sourceTransformer_FileWithoutScriptTag() throws Exception {
		checkTransformation("validUploadConfig.xml", "validUploadConfig.xml",
				RESULT.PORTLET_NO_PROCESSING);
	}

	private static void checkTransformation(String inputName,
			String expectedOutputFile, RESULT expectedResult)
			throws IOException, SAXException {
		SourceTransformer sourceTransformer = new SourceTransformer();

		String inputPath = resolvePath(inputName);

		StringWriter outputWriter = new StringWriter();
		RESULT result = sourceTransformer.transformXMLContent(inputPath,
				new FileReader(inputPath), outputWriter);


		Diff diff = new Diff(
				SourceTransformer
						.readFileToString(resolvePath(expectedOutputFile)),
				outputWriter.toString());
		DetailedDiff detailedDiff = new DetailedDiff(diff);
		boolean identical = diff.identical();
		if (!identical) {
//            String fileName = SourceTransformer.constructAbsolutePath(inputPath, "test.out");
//            System.out.println("fileName = " + fileName);
//            IOUtils.write(outputWriter.toString(),
//                     new FileWriter(fileName)
//             );

			Assert.assertTrue(detailedDiff.toString(), identical);
		}
        Assert.assertEquals(result, expectedResult);
	}

	private static String resolvePath(String inputName) {
		return SourceTransformerTest.class.getClassLoader()
				.getResource(inputName).getPath();
	}

	@Test
	public void sourceTransformer_FileWithScriptTag_WithoutInclude()
			throws Exception {
		checkTransformation("validTwitterConfig.xml", "validTwitterConfig.xml",
				RESULT.PORTLET_NO_PROCESSING);
	}

	@Test
	public void sourceTransformer_FileWithScriptTag_WithInclude()
			throws Exception {
		checkTransformation("validUploadConfig_Include.xml",
				"validUploadConfig_Include_out.xml",
				RESULT.PORTLET_PROCESSING_DONE);
	}

	@Test
	public void sourceTransformer_IncludePathResolution() throws Exception {
		String basePath = "/C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/validUploadConfig_Include.xml";
		checkPathResolution(
				basePath,
				"script.groovy",
				"/C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/script.groovy");

		checkPathResolution(
				basePath,
				"../script.groovy",
				"/C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/script.groovy");

		checkPathResolution(basePath,
				"/C:/Users/serhiy.yevtushenko/script.groovy",
				"/C:/Users/serhiy.yevtushenko/script.groovy");

	}

    @Test
    public void testConstructAbsolutePath() throws IOException {
        String result = SourceTransformer.constructAbsolutePath("C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/",
                "validUploadConfig_Include.xml");
        Assert.assertEquals(new File("C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/validUploadConfig_Include.xml").getCanonicalPath(),
                result);

        result = SourceTransformer.constructAbsolutePath("C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/validUploadConfig_Include.xml",
                "otherConfig.xml");
        Assert.assertEquals(new File("C:/Users/serhiy.yevtushenko/Documents/mvn-plugin/crud2go-maven-plugin/target/test-classes/otherConfig.xml").getCanonicalPath(),
                result);
    }

	@Test
	public void testGetRelativePath() {
		String relativePath = SourceTransformer.getRelativePath(new File(
				"c:\\eai-osiris\\project\\src\\main\\"), new File(
				"c:\\eai-osiris\\project\\src\\main\\de\\portlet.xml"));
		Assert.assertEquals("de/portlet.xml", relativePath);
	}

	private void checkPathResolution(String basePath, String relative,
			String expectedPath) throws IOException {
		String result = SourceTransformer.constructAbsolutePath(basePath,
				relative);
		Assert.assertEquals(new File(expectedPath).getCanonicalPath(), result);
	}

}
