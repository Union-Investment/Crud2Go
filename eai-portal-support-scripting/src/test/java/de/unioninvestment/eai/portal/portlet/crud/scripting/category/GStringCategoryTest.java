package de.unioninvestment.eai.portal.portlet.crud.scripting.category;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import groovy.lang.GString;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.runtime.GStringImpl;
import org.junit.Before;
import org.junit.Test;

public class GStringCategoryTest {

	private GString gsWithNestedNumber;
	private GString gsWithNestedNumberWithoutTrailingString;
	private GroovyShell shell;

	@Before
	public void prepare() {
		shell = new GroovyShell();
		gsWithNestedNumber = (GString) shell.evaluate(" def x=1; \"x = $x\" ");
		// for better understanding
		assertThat(asList(gsWithNestedNumber.getStrings()),
				equalTo(asList("x = ", "")));

		gsWithNestedNumberWithoutTrailingString = new GStringImpl(
				new Object[] { 1 }, new String[] { "x = " });

	}

	@Test
	public void shouldKeepOtherGStringsAsIs() {
		assertThat(GStringCategory.flatten(gsWithNestedNumber),
				is(gsWithNestedNumber));
	}

	@Test
	public void shouldFlattenNestedGString() {
		shell.setProperty("gsWithNestedNumber", gsWithNestedNumber);
		GString gs = (GString) shell
				.evaluate(" \"text is '$gsWithNestedNumber'\" ");

		GString result = GStringCategory.flatten(gs);

		assertThat(asList(result.getStrings()),
				equalTo(asList("text is 'x = ", "'")));
		assertThat(asList(result.getValues()), equalTo(asList((Object) 1)));
	}

	@Test
	public void shouldFlattenNestedGStringWithoutTrailingString() {
		shell.setProperty("gsWithNestedNumberWithoutTrailingString",
				gsWithNestedNumberWithoutTrailingString);
		GString gs = (GString) shell
				.evaluate(" \"text is '$gsWithNestedNumberWithoutTrailingString'\" ");

		GString result = GStringCategory.flatten(gs);

		assertThat(asList(result.getStrings()),
				equalTo(asList("text is 'x = ", "'")));
		assertThat(asList(result.getValues()), equalTo(asList((Object) 1)));
	}

	@Test
	public void shouldFlattenTwoNestedGStrings() {
		shell.setProperty("gsWithNestedNumber", gsWithNestedNumber);
		GString gs = (GString) shell
				.evaluate(" \"two times '$gsWithNestedNumber','$gsWithNestedNumber'\" ");

		GString result = GStringCategory.flatten(gs);

		assertThat(asList(result.getStrings()),
				equalTo(asList("two times 'x = ", "','x = ", "'")));
		assertThat(asList(result.getValues()), equalTo(asList((Object) 1, 1)));
	}

	@Test
	public void shouldFlattenMinimalGString() {
		GString nested = StringCategory.toGString("1,2,3");
		shell.setProperty("nested", nested);
		GString gs = (GString) shell.evaluate(" \"field in ($nested)\" ");

		GString result = GStringCategory.flatten(gs);
		assertThat(asList(result.getStrings()),
				equalTo(asList("field in (1,2,3)")));
		assertThat(result.getValueCount(), is(0));
	}

	@Test
	public void shouldConvertGStringToSqlString() {
		GString test = (GString) shell
				.evaluate(" String a='Test'; \"select * from TEST where a = $a\" ");
		assertThat(GStringCategory.toSqlString(test),
				is("select * from TEST where a = 'Test'"));
	}
}
