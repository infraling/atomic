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
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ID=6, NEWLINE=7, STR=8, WS=9;
	public static final int
		RULE_start = 0, RULE_command = 1, RULE_qname = 2;
	public static final String[] ruleNames = {
		"start", "command", "qname"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'a'", "':'", "'help'", "'clear'", "'::'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "ID", "NEWLINE", "STR", "WS"
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
		try {
			_localctx = new CommanChainContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(6);
			command();
			setState(7);
			match(NEWLINE);
			}
			setState(9);
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
		public Token ID;
		public List<Token> elements = new ArrayList<Token>();
		public Token value;
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
		}
		public TerminalNode STR() { return getToken(ConsoleCommandParser.STR, 0); }
		public List<TerminalNode> ID() { return getTokens(ConsoleCommandParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(ConsoleCommandParser.ID, i);
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
			setState(24);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				_localctx = new AnnotateCommandContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(11);
				match(T__0);
				{
				setState(13); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(12);
						((AnnotateCommandContext)_localctx).ID = match(ID);
						((AnnotateCommandContext)_localctx).elements.add(((AnnotateCommandContext)_localctx).ID);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(15); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				setState(17);
				qname();
				setState(18);
				match(T__1);
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STR) {
					{
					setState(19);
					((AnnotateCommandContext)_localctx).value = match(STR);
					}
				}

				}
				break;
			case T__2:
				_localctx = new HelpCommandContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(22);
				match(T__2);
				}
				break;
			case T__3:
				_localctx = new ClearCommandContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
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

	public static class QnameContext extends ParserRuleContext {
		public Token ns;
		public Token name;
		public List<TerminalNode> ID() { return getTokens(ConsoleCommandParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(ConsoleCommandParser.ID, i);
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
		enterRule(_localctx, 4, RULE_qname);
		try {
			setState(30);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(26);
				((QnameContext)_localctx).ns = match(ID);
				setState(27);
				match(T__4);
				setState(28);
				((QnameContext)_localctx).name = match(ID);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(29);
				((QnameContext)_localctx).name = match(ID);
				}
				break;
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\13#\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\3\3\3\3\6\3\20\n\3\r\3\16\3\21\3\3\3\3"+
		"\3\3\5\3\27\n\3\3\3\3\3\5\3\33\n\3\3\4\3\4\3\4\3\4\5\4!\n\4\3\4\2\2\5"+
		"\2\4\6\2\2$\2\b\3\2\2\2\4\32\3\2\2\2\6 \3\2\2\2\b\t\5\4\3\2\t\n\7\t\2"+
		"\2\n\13\3\2\2\2\13\f\7\2\2\3\f\3\3\2\2\2\r\17\7\3\2\2\16\20\7\b\2\2\17"+
		"\16\3\2\2\2\20\21\3\2\2\2\21\17\3\2\2\2\21\22\3\2\2\2\22\23\3\2\2\2\23"+
		"\24\5\6\4\2\24\26\7\4\2\2\25\27\7\n\2\2\26\25\3\2\2\2\26\27\3\2\2\2\27"+
		"\33\3\2\2\2\30\33\7\5\2\2\31\33\7\6\2\2\32\r\3\2\2\2\32\30\3\2\2\2\32"+
		"\31\3\2\2\2\33\5\3\2\2\2\34\35\7\b\2\2\35\36\7\7\2\2\36!\7\b\2\2\37!\7"+
		"\b\2\2 \34\3\2\2\2 \37\3\2\2\2!\7\3\2\2\2\6\21\26\32 ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}