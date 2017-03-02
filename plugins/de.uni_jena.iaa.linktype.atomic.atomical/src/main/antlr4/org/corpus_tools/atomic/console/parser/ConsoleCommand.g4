// use 'mvn antlr4:antlr4' to re-generate the parser code
grammar ConsoleCommand;



start
	: (command WS* NEWLINE) EOF # CommanChain
	;


command
	: 'a' (elements+=STR)+  anno=qname ':' (value=STR)? # AnnotateCommand
	| 'n' (elements+=STR)* anno_args # NewStructureNodeCommand
	| 's' (elements+=STR)+ anno_args # NewSpanNodeCommand
	| 'd' (elements+=STR)+ # DeleteElementCommand
	| 'e' type=TYPE_STR source=STR target=STR anno=qname ':' (value=STR)? # AddOrDeleteEdgeCommand
	| 'help' # HelpCommand
	| 'clear' # ClearCommand
	;

anno_args
    : anno=qname ':' value=STR
    ;

qname
   : (ns=STR '::')? name=STR
   ;

TYPE_STR : ('-d' | '-p' | '-r' | '-o') ;
NEWLINE : [\r\n]+;
STR : ~(' ' | '\n' | '\r' | ':'  )+ ;
WS : [ \t\r\n]+ -> skip;
