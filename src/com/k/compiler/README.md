# Java Parser

### Description

This parser receive a string(source code) and return a tree of tokens in order with the source code.

###

- Lexical Analyser: analyse tokens and return an ordered array of tokens
- Syntactical Analyser: use and return a tree of grammars

### Implemented Syntax

- For loop, while, if-elseif-else
- Numbers operators (+,-,*,/,%)
- Number comparison

### Grammar

```
<<prog>> → public class <<ID>> { public static void main ( String[] args ) { <<los>> } } 
    program
<<los>> → <<stat>> <<los>> | ε
    
<<stat>> → <<while>> | <<for>> | <<if>> | <<assign>> ; | <<decl>> ; | <<print>> ; | ;
<<while>> → while ( <<rel expr>> <<bool expr>> ) { <<los>> } 
<<for>> → for ( <<for start>> ; <<rel expr>> <<bool expr>> ; <<for arith>> ) { <<los>> } 
<<for start>> → <<decl>> | <<assign>> | ε
<<for arith>> → <<arith expr>> | ε
<<if>> → if ( <<rel expr>> <<bool expr>> ) { <<los>> } <<else if>>
<<else if>> → <<else?if>> { <<los>> } <<else if>> | ε
<<else?if>> → else <<poss if>>
<<poss if>> → if ( <<rel expr>> <<bool expr>> ) | ε
<<assign>> → <<ID>> = <<expr>>
<<decl>> → <<type>> <<ID>> <<poss assign>>
<<poss assign>> → = <<expr>> | ε
<<print>> → System.out.println ( <<print expr>> )
<<type>> → int | boolean | char
<<expr>> → <<rel expr>> <<bool expr>> | <<char expr>>
<<char expr>> → ' <<char>> ' 
<<bool expr>> → <<bool op>> <<rel expr>> <<bool expr>> | ε
<<bool op>> → <<bool eq>> | <<bool log>>
<<bool eq>> → == | != 
<<bool log>> → && | ||
<<rel expr>> → <<arith expr>> <<rel expr'>> | true | false
<<rel expr'>> → <<rel op>> <<arith expr>> | ε
<<rel op>> → < | <= | > | >=
<<arith expr>> → <<term>> <<arith expr'>>
<<arith expr'>> → + <<term>> <<arith expr'>> | - <<term>> <<arith expr'>> | ε
<<term>> → <<factor>> <<term'>>
<<term'>> → * <<factor>> <<term'>> | / <<factor>> <<term'>> | % <<factor>> <<term'>> | ε
<<factor>> → ( <<arith expr>> ) | <<ID>> | <<num>>
<<print expr>> → <<rel expr>> <<bool expr>> | "<<string lit>> "
```

In addition, (and to save confusing EBNF):

<<ID>> is a variable name, has to start with an alphabetic character, and may not include whitespace, any of the
operators, any open or close braces/parenthesis/brackets, and must also be different to any of the keywords (main, if,
while, for, else public, static, void, int, char, boolean, etc. etc.). Note that if you want a functional Java program,
the list of restrictions is the same as for Java.
<<num>> is any integer
<<char>> is just a char literal
<<string lit>> is a string literal

### Symbols

```
+
-
*
/
%
=
==
!=
<
>
<=
>=
(
)
{
}
&& 
|| 
; 
public 
class 
static 
void 
main 
String[] 
args 
int 
boolean 
char 
System.out.println 
while 
for 
if 
else 
" 
' 
```

### Acknowledgement

Theory of Computing Science - UTS
