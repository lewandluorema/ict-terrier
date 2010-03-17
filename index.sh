#!/bin/sh

java -cp "src:ict-terrier.jar:`find lib -name '*.jar' | perl -e'print join(":", map { chomp; $_ } <>), "\n"'`" \
    edu.usc.ict.StoryIndexer $*
