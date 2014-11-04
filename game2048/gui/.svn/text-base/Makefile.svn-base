SHELL = bash

STYLEPROG = style61b

PACKAGE = game2048.gui

# Flags to pass to Java compilations (include debugging info and report
# "unsafe" operations.)
JFLAGS = -g -Xlint:unchecked -cp ../..:$(CLASSPATH) -d ../..

SRCS = $(wildcard *.java)

CLASSES = $(SRCS:.java=.class)

# Tell make that these are not really files.
.PHONY: clean default compile style

# By default, make sure all classes are present and check if any sources have
# changed since the last build.
default: compile

compile: $(CLASSES)

style:
	$(STYLEPROG) $(SRCS) 

$(CLASSES): sentinel

sentinel: $(SRCS)
	javac $(JFLAGS) $(SRCS)
	touch $@

clean:
	$(RM) sentinel *.class
	$(RM) *~

# Include additions to standard Makefile, if any.
-include Makefile.local
