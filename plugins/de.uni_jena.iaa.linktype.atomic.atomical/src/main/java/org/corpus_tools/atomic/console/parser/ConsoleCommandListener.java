// Generated from org/corpus_tools/atomic/console/parser/ConsoleCommand.g4 by ANTLR 4.6
package org.corpus_tools.atomic.console.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConsoleCommandParser}.
 */
public interface ConsoleCommandListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code CommanChain}
	 * labeled alternative in {@link ConsoleCommandParser#start}.
	 * @param ctx the parse tree
	 */
	void enterCommanChain(ConsoleCommandParser.CommanChainContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CommanChain}
	 * labeled alternative in {@link ConsoleCommandParser#start}.
	 * @param ctx the parse tree
	 */
	void exitCommanChain(ConsoleCommandParser.CommanChainContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AnnotateCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterAnnotateCommand(ConsoleCommandParser.AnnotateCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AnnotateCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitAnnotateCommand(ConsoleCommandParser.AnnotateCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewStructureNodeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterNewStructureNodeCommand(ConsoleCommandParser.NewStructureNodeCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewStructureNodeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitNewStructureNodeCommand(ConsoleCommandParser.NewStructureNodeCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewSpanNodeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterNewSpanNodeCommand(ConsoleCommandParser.NewSpanNodeCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewSpanNodeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitNewSpanNodeCommand(ConsoleCommandParser.NewSpanNodeCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeleteElementCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterDeleteElementCommand(ConsoleCommandParser.DeleteElementCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeleteElementCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitDeleteElementCommand(ConsoleCommandParser.DeleteElementCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddOrDeleteEdgeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterAddOrDeleteEdgeCommand(ConsoleCommandParser.AddOrDeleteEdgeCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddOrDeleteEdgeCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitAddOrDeleteEdgeCommand(ConsoleCommandParser.AddOrDeleteEdgeCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code HelpCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterHelpCommand(ConsoleCommandParser.HelpCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code HelpCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitHelpCommand(ConsoleCommandParser.HelpCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ClearCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void enterClearCommand(ConsoleCommandParser.ClearCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ClearCommand}
	 * labeled alternative in {@link ConsoleCommandParser#command}.
	 * @param ctx the parse tree
	 */
	void exitClearCommand(ConsoleCommandParser.ClearCommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConsoleCommandParser#anno_args}.
	 * @param ctx the parse tree
	 */
	void enterAnno_args(ConsoleCommandParser.Anno_argsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConsoleCommandParser#anno_args}.
	 * @param ctx the parse tree
	 */
	void exitAnno_args(ConsoleCommandParser.Anno_argsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConsoleCommandParser#qname}.
	 * @param ctx the parse tree
	 */
	void enterQname(ConsoleCommandParser.QnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConsoleCommandParser#qname}.
	 * @param ctx the parse tree
	 */
	void exitQname(ConsoleCommandParser.QnameContext ctx);
}