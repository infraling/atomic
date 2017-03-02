// use 'mvn antlr4:antlr4' to re-generate the parser code
grammar ConsoleCommand;



start
	: (command WS* NEWLINE) EOF # CommanChain
	;


command
	: 'a' (elements+=STR)+  qname ':' (value=STR)? # AnnotateCommand
	| 'help' # HelpCommand
	| 'clear' # ClearCommand
	;

annotate_args
    :
    ;

qname
   : (ns=STR '::')? name=STR
   ;


NEWLINE : [\r\n]+;
STR : ~(' ' | '\n' | '\r' | ':'  )+ ;
WS : [ \t\r\n]+ -> skip;
