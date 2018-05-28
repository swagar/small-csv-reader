package de.wagar.csv;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.wagar.csv.CsvReader.CsvLine;

public class CsvReaderTest {
  private File csvFileWithHeader = new File("src/test/resources/csvFileWithHeader.csv");
  private File csvFileWithoutHeader = new File("src/test/resources/csvFileWithoutHeader.csv");

  @Test
  public void testThatReaderReadsHeaderCsvFile() {
    // given
    File csvFile = csvFileWithHeader;
    CsvReader reader = CsvReader
        .create()
        .withHeader()
        .file(csvFile)
        .splitChar(",")
        .build();

    // when
    CsvLine line = reader.getNextLine();
    reader.close();

    // then
    assertThat(line, notNullValue());
    assertThat(line.getRow().length, is(3));
    assertThat(line.getField("ID"), is(equalTo("101")));
    assertThat(line.getField("FIRST_NAME"), is(equalTo("Foo")));
    assertThat(line.getRow()[2], is(equalTo("Bar")));
  }

  @Test
  public void testThatReaderUsesProvidedArrayHeader() {
    // given
    File csvFile = csvFileWithoutHeader;
    String[] providedHeader = {"id", "firstName", "lastName"};
    CsvReader reader = CsvReader
        .create()
        .withoutHeader()
        .withProvidedHeader(providedHeader)
        .file(csvFile)
        .splitChar(",")
        .build();

    // when
    CsvLine line = reader.getNextLine();
    reader.close();

    // then
    assertThat(line, notNullValue());
    assertThat(line.getRow().length, is(3));
    assertThat(line.getField("id"), is(equalTo("101")));
    assertThat(line.getField("firstName"), is(equalTo("Foo")));
    assertThat(line.getRow()[2], is(equalTo("Bar")));
  }

  @Test
  public void testThatReaderUsesProvidedMapHeader() {
    // given
    File csvFile = csvFileWithoutHeader;
    Map<String, Integer> providedHeader = new HashMap<>();
    providedHeader.put("id", 0);
    providedHeader.put("firstName", 1);
    providedHeader.put("lastName", 2);

    CsvReader reader = CsvReader
        .create()
        .withoutHeader()
        .withProvidedHeader(providedHeader)
        .file(csvFile)
        .splitChar(",")
        .build();

    // when
    CsvLine line = reader.getNextLine();
    reader.close();

    // then
    assertThat(line, notNullValue());
    assertThat(line.getRow().length, is(3));
    assertThat(line.getField("id"), is(equalTo("101")));
    assertThat(line.getField("firstName"), is(equalTo("Foo")));
    assertThat(line.getRow()[2], is(equalTo("Bar")));
  }
}
