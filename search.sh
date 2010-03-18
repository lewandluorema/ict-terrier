#!/bin/sh

java -cp "ict-terrier.jar:`find lib -name '*.jar' | perl -e'print join(":", map { chomp; $_ } <>), "\n"'`" \
    edu.usc.ict.StorySearcher $*
