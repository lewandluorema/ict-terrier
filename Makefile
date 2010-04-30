#--------------------------------------------------
# Path to programs
#-------------------------------------------------- 
JC = /usr/bin/javac
JAR = /usr/bin/jar
PERL = /usr/bin/perl
CTAGS = /usr/bin/ctags

# CLASSPATH=lib/antlr.jar:lib/log4j-1.2.9.jar:lib/terrier-2.2.1.jar:lib/trove-2.0.2.jar:ict-terrier.jar
CLASSPATH=$(shell find lib -name '*.jar' | $(PERL) -e'@l = map { chomp; $$_ } <>; print join ":", @l; print "\n"')
SRC = $(shell find src -name '*.java')

JFLAGS = -cp $(CLASSPATH):src
BUILD = build
BUNDLE = ict-terrier.jar

#--------------------------------------------------
# Rules
#-------------------------------------------------- 
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

.PHONY: classes jar clean all

all: jar

jar: $(BUNDLE)

$(BUNDLE): $(SRC:.java=.class)
	(cd src; $(JAR) cvfM $(BUNDLE) $(shell cd src; find -name '*.class'))
	mv src/$(BUNDLE) .

#--------------------------------------------------
# Tags support are optional
#-------------------------------------------------- 
tags:
	$(CTAGS) -R src

clean:
	rm -f $(shell find src -name '*.class')
