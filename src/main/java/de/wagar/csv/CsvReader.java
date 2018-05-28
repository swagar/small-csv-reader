package de.wagar.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CsvReader {
  private String splitChar;
  private Map<String, Integer> header;
  private BufferedReader br;

  public CsvReader(String splitChar, boolean hasHeader, Map<String, Integer> providedHeader, File csvFile)
      throws IOException {
    this.splitChar = splitChar;
    this.br = new BufferedReader(new FileReader(csvFile));

    if (hasHeader) {
      this.header = readHeader();
    } else {
      this.header = providedHeader;
    }
  }

  private Map<String, Integer> readHeader() throws IOException {
    String lineAsString = br.readLine();

    return convertArrayToMap(lineAsString.split(splitChar));
  }

  public Stream<CsvLine> stream() {
    return br.lines().map(l -> new CsvLine(l.split(splitChar)));
  }

  public CsvLine getNextLine() {
    try {
      return new CsvLine(br.readLine().split(splitChar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void close() {
    try {
      br.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public class CsvLine {
    private String[] fields;

    public CsvLine(String[] fields) {
      this.fields = fields;
    }

    public String[] getRow() {
      return fields;
    }

    public String getField(String fieldName) {
      if (null == header) {
        throw new RuntimeException("getField is only usable when there is a header");
      }

      return fields[header.get(fieldName)];
    }
  }

  public static class Builder {
    private String splitChar;
    private boolean hasHeader;
    private Map<String, Integer> header;
    private File csvFile;

    public Builder withHeader() {
      hasHeader = true;

      return this;
    }

    public Builder withoutHeader() {
      hasHeader = false;

      return this;
    }

    public Builder withProvidedHeader(Map<String, Integer> header) {
      hasHeader = false;
      this.header = header;

      return this;
    }

    public Builder withProvidedHeader(String[] header) {
      hasHeader = false;

      this.header = convertArrayToMap(header);

      return this;
    }

    public Builder splitChar(String splitChar) {
      if (1 != splitChar.length()) {
        throw new InvalidParameterException("splitChar must be of length 1 but it is " + splitChar.length());
      }
      this.splitChar = splitChar;

      return this;
    }

    public Builder file(String pathToCsvFile) {
      this.csvFile = new File(pathToCsvFile);

      return this;
    }

    public Builder file(File csvFile) {
      this.csvFile = csvFile;

      return this;
    }

    public CsvReader build() {
      try {
        return new CsvReader(splitChar, hasHeader, header, csvFile);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  public static Builder create() {
    return new Builder();
  }

  public static Map<String, Integer> convertArrayToMap(String[] array) {
    Map<String, Integer> map = new HashMap<>();

    int arrayLength = array.length;
    for (int i = 0; i < arrayLength; ++i) {
      String column = array[i];

      map.put(column, i);
    }

    return map;
  }
}
