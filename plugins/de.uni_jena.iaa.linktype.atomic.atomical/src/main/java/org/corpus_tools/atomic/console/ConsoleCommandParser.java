// Generated from org/corpus_tools/atomic/console/ConsoleCommand.g4 by ANTLR 4.6
package org.corpus_tools.atomic.console;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConsoleCommandParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, NEWLINE=6, STR=7, WS=8;
	public static final int
		RULE_start = 0, RULE_command = 1, RULE_annotate_args = 2, RULE_qname = 3;
	public static final String[] ruleNames = {
		"start", "command", "annotate_args", "qname"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'a'", "':'", "'help'", "'clear'", "'::'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "NEWLINE", "STR", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ConsoleCommand.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ConsoleCommandParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StartContext extends ParserRuleContext {
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
	 
		public StartContext() { }
		public void copyFrom(StartContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CommanChainContext extends StartContext {
		public TerminalNode EOF() { return getToken(ConsoleCommandParser.EOF, 0); }
		public CommandContext command() {
			return getRuleContext(CommandContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(ConsoleCommandParser.NEWLINE, 0); }
		public List<TerminalNode> WS() { return getTokens(ConsoleCommandParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(ConsoleCommandParser.WS, i);
		}
		public CommanChainContext(StartContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterCommanChain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitCommanChain(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_start);
		int _la;
		try {
			_localctx = new CommanChainContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(8);
			command();
			setState(12);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(9);
				match(WS);
				}
				}
				setState(14);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(15);
			match(NEWLINE);
			}
			setState(17);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandContext extends ParserRuleContext {
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
	 
		public CommandContext() { }
		public void copyFrom(CommandContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class HelpCommandContext extends CommandContext {
		public HelpCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterHelpCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitHelpCommand(this);
		}
	}
	public static class ClearCommandContext extends CommandContext {
		public ClearCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterClearCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitClearCommand(this);
		}
	}
	public static class AnnotateCommandContext extends CommandContext {
		public Token STR;
		public List<Token> elements = new ArrayList<Token>();
		public Token value;
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
		}
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public AnnotateCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterAnnotateCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitAnnotateCommand(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_command);
		int _la;
		try {
			int _alt;
			setState(32);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				_localctx = new AnnotateCommandContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(19);
				match(T__0);
				setState(21); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(20);
						((AnnotateCommandContext)_localctx).STR = match(STR);
						((AnnotateCommandContext)_localctx).elements.add(((AnnotateCommandContext)_localctx).STR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(23); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(25);
				qname();
				setState(26);
				match(T__1);
				setState(28);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STR) {
					{
					setState(27);
					((AnnotateCommandContext)_localctx).value = match(STR);
					}
				}

				}
				break;
			case T__2:
				_localctx = new HelpCommandContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(30);
				match(T__2);
				}
				break;
			case T__3:
				_localctx = new ClearCommandContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(31);
				match(T__3);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Annotate_argsContext extends ParserRuleContext {
		public Annotate_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_annotate_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterAnnotate_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitAnnotate_args(this);
		}
	}

	public final Annotate_argsContext annotate_args() throws RecognitionException {
		Annotate_argsContext _localctx = new Annotate_argsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_annotate_args);
		try {
			enterOuterAlt(_localctx, 1);
			{
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QnameContext extends ParserRuleContext {
		public Token ns;
		public Token name;
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public QnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qname; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterQname(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitQname(this);
		}
	}

	public final QnameContext qname() throws RecognitionException {
		QnameContext _localctx = new QnameContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_qname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(36);
				((QnameContext)_localctx).ns = match(STR);
				setState(37);
				match(T__4);
				}
				break;
			}
			setState(40);
			((QnameContext)_localctx).name = match(STR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\n-\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\7\2\r\n\2\f\2\16\2\20\13\2\3\2\3\2\3\2\3\2"+
		"\3\3\3\3\6\3\30\n\3\r\3\16\3\31\3\3\3\3\3\3\5\3\37\n\3\3\3\3\3\5\3#\n"+
		"\3\3\4\3\4\3\5\3\5\5\5)\n\5\3\5\3\5\3\5\2\2\6\2\4\6\b\2\2.\2\n\3\2\2\2"+
		"\4\"\3\2\2\2\6$\3\2\2\2\b(\3\2\2\2\n\16\5\4\3\2\13\r\7\n\2\2\f\13\3\2"+
		"\2\2\r\20\3\2\2\2\16\f\3\2\2\2\16\17\3\2\2\2\17\21\3\2\2\2\20\16\3\2\2"+
		"\2\21\22\7\b\2\2\22\23\3\2\2\2\23\24\7\2\2\3\24\3\3\2\2\2\25\27\7\3\2"+
		"\2\26\30\7\t\2\2\27\26\3\2\2\2\30\31\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2"+
		"\2\32\33\3\2\2\2\33\34\5\b\5\2\34\36\7\4\2\2\35\37\7\t\2\2\36\35\3\2\2"+
		"\2\36\37\3\2\2\2\37#\3\2\2\2 #\7\5\2\2!#\7\6\2\2\"\25\3\2\2\2\" \3\2\2"+
		"\2\"!\3\2\2\2#\5\3\2\2\2$%\3\2\2\2%\7\3\2\2\2&\'\7\t\2\2\')\7\7\2\2(&"+
		"\3\2\2\2()\3\2\2\2)*\3\2\2\2*+\7\t\2\2+\t\3\2\2\2\7\16\31\36\"(";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}