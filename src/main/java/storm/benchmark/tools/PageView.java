package storm.benchmark.tools;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.List;

public class PageView {
  public final String url;
  public final int status;
  public final int zipCode;
  public final int userID;

  public PageView(String url, int status, int zipCode, int userID) {
    this.url = url;
    this.status = status;
    this.zipCode = zipCode;
    this.userID = userID;
  }

  @Override
  public String toString() {
    return String.format("%s\t%d\t%d\t%d\n", url, status, zipCode, userID);
  }

  public static PageView fromString(String s) {
    String[] parts = s.split("\t");
    if (parts.length < 4) {
      return null;
    }
    return new PageView(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
  }

  public Object getValue(Item field) {
    switch (field) {
      case URL:
        return url;
      case STATUS:
        return status;
      case ZIP:
        return zipCode;
      case USER:
        return userID;
      case ONE:
        return 1;
      default:
        return toString();
    }
  }

  public static enum Item {
    ALL("page_view"),
    URL("url"),
    STATUS("http_status"),
    ZIP("zip_code"),
    USER("user_id"),
    ONE("count_one");

    private final String name;

    Item(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static class Extract extends BaseFunction {

    private final List<Item> fields;

    public Extract(List<Item> fields) {
      this.fields = fields;
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
      PageView pageView = PageView.fromString(tuple.getString(0));
      List<Object> values = new ArrayList<Object>();
      for (Item field : fields) {
        values.add(pageView.getValue(field));
      }
      collector.emit(values);
    }
  }
}
