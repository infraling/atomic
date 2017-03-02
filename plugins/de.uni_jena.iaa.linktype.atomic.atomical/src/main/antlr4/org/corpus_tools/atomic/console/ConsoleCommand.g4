// use 'mvn antlr4:antlr4' to re-generate the parser code
grammar ConsoleCommand;


start
	: (command NEWLINE) EOF # CommanChain
	;


command
	: 'a' (elements+=ID+) qname ':' (value=STR)? # AnnotateCommand
	| 'help' # HelpCommand
	| 'clear' # ClearCommand
	;

qname
   : ns=ID '::' name=ID
   | name=ID
   ;

ID  :	[a-zA-Z0-9_-]+;
NEWLINE : [\r\n]+ ;
STR : [^ \t\r\n\u000C]+ ;
WS : [ \t\r\n\u000C]+ -> skip;
