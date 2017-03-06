// use 'mvn antlr4:antlr4' to re-generate the parser code
grammar ConsoleCommand;

options
{
  language=Java;
}


start
	: (command WS* NEWLINE) EOF # CommanChain
	;


command
	: {"a".equals(getCurrentToken().getText())}? cmd=STR (elements+=STR)+  anno=qname ':' (value=STR)? # AnnotateCommand
	| {"n".equals(getCurrentToken().getText())}? cmd=STR (elements+=STR)* anno_args # NewStructureNodeCommand
	| {"s".equals(getCurrentToken().getText())}? cmd=STR (elements+=STR)+ anno_args # NewSpanNodeCommand
	| {"d".equals(getCurrentToken().getText())}? cmd=STR (elements+=STR)+ # DeleteElementCommand
	| {"e".equals(getCurrentToken().getText())}? cmd=STR type=TYPE_STR source=STR target=STR anno=qname ':' (value=STR)? # AddOrDeleteEdgeCommand
	| {"help".equals(getCurrentToken().getText())}? cmd=STR # HelpCommand
	| {"clear".equals(getCurrentToken().getText())}? cmd=STR # ClearCommand
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
