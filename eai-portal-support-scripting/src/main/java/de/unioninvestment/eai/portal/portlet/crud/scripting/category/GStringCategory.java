package de.unioninvestment.eai.portal.portlet.crud.scripting.category;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.StringWriterIOException;
import groovy.sql.Sql;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Extensions for GStrings.
 * 
 * @author carsten.mjartan
 */
public class GStringCategory {

	/**
	 * Creates a new GString, with nested GStrings pulled to the top Level. This
	 * can be used in combination with {@link Sql} to execute nested Statements.
	 * Sub-GStrings are NOT flattened recursively.
	 * 
	 * @param root
	 *            the GString to flatten
	 * @return the flattened result
	 */
	public static GString flatten(GString root) {
		Object[] values = root.getValues();
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof GString) {
				GString head = head(root, i);
				GString sub = (GString) values[i];
				GString tail = tail(root, i);
				return head.plus(sub).plus(flatten(tail));
			}
		}
		return root;
	}

	private static GString head(GString root, int valueIndex) {
		String[] headStrings = new String[valueIndex + 1];
		Object[] headValues = new Object[valueIndex];
		System.arraycopy(root.getStrings(), 0, headStrings, 0, valueIndex + 1);
		System.arraycopy(root.getValues(), 0, headValues, 0, valueIndex);
		return new GStringImpl(headValues, headStrings);
	}

	private static GString tail(GString root, int valueIndex) {
		String[] strings = root.getStrings();
		Object[] values = root.getValues();

		int tailStringLength = strings.length - valueIndex - 1;
		int tailValueLength = values.length - valueIndex - 1;

		String[] tailStrings = new String[tailStringLength];
		Object[] tailValues = new Object[tailValueLength];

		System.arraycopy(strings, valueIndex + 1, tailStrings, 0,
				tailStringLength);
		System.arraycopy(values, valueIndex + 1, tailValues, 0, tailValueLength);

		return new GStringImpl(tailValues, tailStrings);
	}

	/**
	 * This Method tries to convert an SQL GString to an SQL Statement for
	 * easier readability (of the audit log).
	 * 
	 * Functionality mostly copied from GString.toString(). Currently only
	 * String Parameters are encapsulated
	 * 
	 * @param gs
	 *            the input GString
	 * @return a String of what an SQL could look like.
	 */
	public static String toSqlString(GString gs) {
		StringWriter out = new StringWriter();
		try {
			String[] s = gs.getStrings();
			int numberOfValues = gs.getValueCount();

			for (int i = 0, size = s.length; i < size; i++) {
				out.write(s[i]);

				if (i < numberOfValues) {
					final Object value = gs.getValue(i);

					if (value instanceof Closure) {
						final Closure c = (Closure) value;

						if (c.getMaximumNumberOfParameters() == 0) {
							writeSqlParameter(out, c.call());

						} else if (c.getMaximumNumberOfParameters() == 1) {
							c.call(out);

						} else {
							throw new GroovyRuntimeException(
									"Trying to evaluate a GString containing a Closure taking "
											+ c.getMaximumNumberOfParameters()
											+ " parameters");
						}
					} else {
						writeSqlParameter(out, value);
					}
				}
			}
		} catch (IOException e) {
			throw new StringWriterIOException(e);
		}
		return out.toString();
	}

	private static void writeSqlParameter(StringWriter out, Object value)
			throws IOException {

		if (value instanceof String || value instanceof GString
				|| value instanceof Reader) {
			out.write("'");
			InvokerHelper.write(out, value);
			out.write("'");
		} else {
			InvokerHelper.write(out, value);
		}
	}
}
