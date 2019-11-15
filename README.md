# lsp4xml-AtSec-Edition

This is a edited version of the "XML Language Server".

We have made changes to the following files:

    org.eclipse.lsp4xml/src/main/java/org/eclipse/lsp4xml/services/XMLFormatter.java
    org.eclipse.lsp4xml/src/main/java/org/eclipse/lsp4xml/utils/StringUtils.java
    org.eclipse.lsp4xml/src/main/java/org/eclipse/lsp4xml/utils/XMLBuilder.java

Before our changes, the tag elements would always be put into a new Line. This makes the readability of varoius XML-Files much harder and less natural.

This is why we changed this to have the tag elements stay on the same line.

We hope this can be implemented soon by our friends at RedHat.

