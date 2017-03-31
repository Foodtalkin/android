package in.foodtalk.android.helper;

import android.content.Context;

import com.google.android.gms.analytics.StandardExceptionParser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * Created by RetailAdmin on 30-03-2017.
 */

public class AnalyticsExceptionParser extends StandardExceptionParser {
    public AnalyticsExceptionParser(Context context, Collection<String> collection) {
        super(context, collection);
    }

    @Override
    protected String getDescription(Throwable throwable, StackTraceElement stackTraceElement, String s) {
        StringBuilder descriptionBuilder = new StringBuilder();
        final Writer writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        descriptionBuilder.append(writer.toString());
        printWriter.close();


        return descriptionBuilder.toString();
    }
}
