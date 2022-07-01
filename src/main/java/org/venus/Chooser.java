package org.venus;

public sealed interface Chooser permits ChooserBase {

    Protocol choose(Request request);
}
