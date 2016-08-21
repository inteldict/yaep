# YAEP - Yet Another Earley Parser

This is a vanilla **Java 8** implementation of the **Earley Parser**. The Parser executes in quadratic time *O(n^2)* (with unambiguous grammars)
and cubic time *O(n^3)* in the worst case (*'n'* - is the length of the input).

## Grammar

The parser is suitable for processing any context-free language using an appropriate context-free grammar (CFG).
Unlike LL, LR, LALR parsers, requirements for the Earley Parser grammar are more general and a grammar can be
 easily written by hand.

 At present the parser can load grammars in the form:

 ```
 # grammar

 S -> NP VP
 NP -> NP PP
 NP -> Noun
 VP -> VP PP
 VP -> Verb NP
 PP -> Prep NP

 # lexicon

 Noun -> "Jan" | 'Mary' | "Frankfurt"
 Verb -> "called"
 Prep -> "from"
 ```

The implementation includes a grammar loader. See *resources* for grammar examples.


## The parsing algorithm

You can find a detailed algorithm description here:

[Jurafsky D., Martin J.H. Speech and Language Processing: An Introduction to Natural Language Processing, Computational Linguistics, and Speech Recognition](http://amzn.to/2bxdt0g)

 qooxdoo application skeleton which is used as a template. The
'create-application.py' script (usually under tool/bin/create-application.py)
will use this and expand it into a self-contained qooxdoo application which
can then be further extended. Please refer to the script and other documentation
for further information.
