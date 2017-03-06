// Generated from org/corpus_tools/atomic/console/parser/ConsoleCommand.g4 by ANTLR 4.6
package org.corpus_tools.atomic.console.parser;
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
		T__0=1, T__1=2, TYPE_STR=3, NEWLINE=4, STR=5, WS=6;
	public static final int
		RULE_start = 0, RULE_command = 1, RULE_anno_args = 2, RULE_qname = 3;
	public static final String[] ruleNames = {
		"start", "command", "anno_args", "qname"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "':'", "'::'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "TYPE_STR", "NEWLINE", "STR", "WS"
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
	public static class NewStructureNodeCommandContext extends CommandContext {
		public Token cmd;
		public Token STR;
		public List<Token> elements = new ArrayList<Token>();
		public Anno_argsContext anno_args() {
			return getRuleContext(Anno_argsContext.class,0);
		}
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public NewStructureNodeCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterNewStructureNodeCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitNewStructureNodeCommand(this);
		}
	}
	public static class DeleteElementCommandContext extends CommandContext {
		public Token cmd;
		public Token STR;
		public List<Token> elements = new ArrayList<Token>();
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public DeleteElementCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterDeleteElementCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitDeleteElementCommand(this);
		}
	}
	public static class AddOrDeleteEdgeCommandContext extends CommandContext {
		public Token cmd;
		public Token type;
		public Token source;
		public Token target;
		public QnameContext anno;
		public Token value;
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public TerminalNode TYPE_STR() { return getToken(ConsoleCommandParser.TYPE_STR, 0); }
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
		}
		public AddOrDeleteEdgeCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterAddOrDeleteEdgeCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitAddOrDeleteEdgeCommand(this);
		}
	}
	public static class HelpCommandContext extends CommandContext {
		public Token cmd;
		public TerminalNode STR() { return getToken(ConsoleCommandParser.STR, 0); }
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
		public Token cmd;
		public TerminalNode STR() { return getToken(ConsoleCommandParser.STR, 0); }
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
		public Token cmd;
		public Token STR;
		public List<Token> elements = new ArrayList<Token>();
		public QnameContext anno;
		public Token value;
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
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
	public static class NewSpanNodeCommandContext extends CommandContext {
		public Token cmd;
		public Token STR;
		public List<Token> elements = new ArrayList<Token>();
		public Anno_argsContext anno_args() {
			return getRuleContext(Anno_argsContext.class,0);
		}
		public List<TerminalNode> STR() { return getTokens(ConsoleCommandParser.STR); }
		public TerminalNode STR(int i) {
			return getToken(ConsoleCommandParser.STR, i);
		}
		public NewSpanNodeCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterNewSpanNodeCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitNewSpanNodeCommand(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_command);
		int _la;
		try {
			int _alt;
			setState(69);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new AnnotateCommandContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(19);
				if (!("a".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"a\".equals(getCurrentToken().getText())");
				setState(20);
				((AnnotateCommandContext)_localctx).cmd = match(STR);
				setState(22); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(21);
						((AnnotateCommandContext)_localctx).STR = match(STR);
						((AnnotateCommandContext)_localctx).elements.add(((AnnotateCommandContext)_localctx).STR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(24); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(26);
				((AnnotateCommandContext)_localctx).anno = qname();
				setState(27);
				match(T__0);
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STR) {
					{
					setState(28);
					((AnnotateCommandContext)_localctx).value = match(STR);
					}
				}

				}
				break;
			case 2:
				_localctx = new NewStructureNodeCommandContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(31);
				if (!("n".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"n\".equals(getCurrentToken().getText())");
				setState(32);
				((NewStructureNodeCommandContext)_localctx).cmd = match(STR);
				setState(36);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(33);
						((NewStructureNodeCommandContext)_localctx).STR = match(STR);
						((NewStructureNodeCommandContext)_localctx).elements.add(((NewStructureNodeCommandContext)_localctx).STR);
						}
						} 
					}
					setState(38);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
				}
				setState(39);
				anno_args();
				}
				break;
			case 3:
				_localctx = new NewSpanNodeCommandContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(40);
				if (!("s".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"s\".equals(getCurrentToken().getText())");
				setState(41);
				((NewSpanNodeCommandContext)_localctx).cmd = match(STR);
				setState(43); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(42);
						((NewSpanNodeCommandContext)_localctx).STR = match(STR);
						((NewSpanNodeCommandContext)_localctx).elements.add(((NewSpanNodeCommandContext)_localctx).STR);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(45); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(47);
				anno_args();
				}
				break;
			case 4:
				_localctx = new DeleteElementCommandContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(48);
				if (!("d".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"d\".equals(getCurrentToken().getText())");
				setState(49);
				((DeleteElementCommandContext)_localctx).cmd = match(STR);
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(50);
					((DeleteElementCommandContext)_localctx).STR = match(STR);
					((DeleteElementCommandContext)_localctx).elements.add(((DeleteElementCommandContext)_localctx).STR);
					}
					}
					setState(53); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==STR );
				}
				break;
			case 5:
				_localctx = new AddOrDeleteEdgeCommandContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(55);
				if (!("e".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"e\".equals(getCurrentToken().getText())");
				setState(56);
				((AddOrDeleteEdgeCommandContext)_localctx).cmd = match(STR);
				setState(57);
				((AddOrDeleteEdgeCommandContext)_localctx).type = match(TYPE_STR);
				setState(58);
				((AddOrDeleteEdgeCommandContext)_localctx).source = match(STR);
				setState(59);
				((AddOrDeleteEdgeCommandContext)_localctx).target = match(STR);
				setState(60);
				((AddOrDeleteEdgeCommandContext)_localctx).anno = qname();
				setState(61);
				match(T__0);
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STR) {
					{
					setState(62);
					((AddOrDeleteEdgeCommandContext)_localctx).value = match(STR);
					}
				}

				}
				break;
			case 6:
				_localctx = new HelpCommandContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(65);
				if (!("help".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"help\".equals(getCurrentToken().getText())");
				setState(66);
				((HelpCommandContext)_localctx).cmd = match(STR);
				}
				break;
			case 7:
				_localctx = new ClearCommandContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(67);
				if (!("clear".equals(getCurrentToken().getText()))) throw new FailedPredicateException(this, "\"clear\".equals(getCurrentToken().getText())");
				setState(68);
				((ClearCommandContext)_localctx).cmd = match(STR);
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

	public static class Anno_argsContext extends ParserRuleContext {
		public QnameContext anno;
		public Token value;
		public QnameContext qname() {
			return getRuleContext(QnameContext.class,0);
		}
		public TerminalNode STR() { return getToken(ConsoleCommandParser.STR, 0); }
		public Anno_argsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anno_args; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).enterAnno_args(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConsoleCommandListener ) ((ConsoleCommandListener)listener).exitAnno_args(this);
		}
	}

	public final Anno_argsContext anno_args() throws RecognitionException {
		Anno_argsContext _localctx = new Anno_argsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_anno_args);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			((Anno_argsContext)_localctx).anno = qname();
			setState(72);
			match(T__0);
			setState(73);
			((Anno_argsContext)_localctx).value = match(STR);
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
			setState(77);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(75);
				((QnameContext)_localctx).ns = match(STR);
				setState(76);
				match(T__1);
				}
				break;
			}
			setState(79);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return command_sempred((CommandContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean command_sempred(CommandContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return "a".equals(getCurrentToken().getText());
		case 1:
			return "n".equals(getCurrentToken().getText());
		case 2:
			return "s".equals(getCurrentToken().getText());
		case 3:
			return "d".equals(getCurrentToken().getText());
		case 4:
			return "e".equals(getCurrentToken().getText());
		case 5:
			return "help".equals(getCurrentToken().getText());
		case 6:
			return "clear".equals(getCurrentToken().getText());
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\bT\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\7\2\r\n\2\f\2\16\2\20\13\2\3\2\3\2\3\2\3\2"+
		"\3\3\3\3\3\3\6\3\31\n\3\r\3\16\3\32\3\3\3\3\3\3\5\3 \n\3\3\3\3\3\3\3\7"+
		"\3%\n\3\f\3\16\3(\13\3\3\3\3\3\3\3\3\3\6\3.\n\3\r\3\16\3/\3\3\3\3\3\3"+
		"\3\3\6\3\66\n\3\r\3\16\3\67\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3B\n\3\3"+
		"\3\3\3\3\3\3\3\5\3H\n\3\3\4\3\4\3\4\3\4\3\5\3\5\5\5P\n\5\3\5\3\5\3\5\2"+
		"\2\6\2\4\6\b\2\2]\2\n\3\2\2\2\4G\3\2\2\2\6I\3\2\2\2\bO\3\2\2\2\n\16\5"+
		"\4\3\2\13\r\7\b\2\2\f\13\3\2\2\2\r\20\3\2\2\2\16\f\3\2\2\2\16\17\3\2\2"+
		"\2\17\21\3\2\2\2\20\16\3\2\2\2\21\22\7\6\2\2\22\23\3\2\2\2\23\24\7\2\2"+
		"\3\24\3\3\2\2\2\25\26\6\3\2\2\26\30\7\7\2\2\27\31\7\7\2\2\30\27\3\2\2"+
		"\2\31\32\3\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2\33\34\3\2\2\2\34\35\5\b\5"+
		"\2\35\37\7\3\2\2\36 \7\7\2\2\37\36\3\2\2\2\37 \3\2\2\2 H\3\2\2\2!\"\6"+
		"\3\3\2\"&\7\7\2\2#%\7\7\2\2$#\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'"+
		")\3\2\2\2(&\3\2\2\2)H\5\6\4\2*+\6\3\4\2+-\7\7\2\2,.\7\7\2\2-,\3\2\2\2"+
		"./\3\2\2\2/-\3\2\2\2/\60\3\2\2\2\60\61\3\2\2\2\61H\5\6\4\2\62\63\6\3\5"+
		"\2\63\65\7\7\2\2\64\66\7\7\2\2\65\64\3\2\2\2\66\67\3\2\2\2\67\65\3\2\2"+
		"\2\678\3\2\2\28H\3\2\2\29:\6\3\6\2:;\7\7\2\2;<\7\5\2\2<=\7\7\2\2=>\7\7"+
		"\2\2>?\5\b\5\2?A\7\3\2\2@B\7\7\2\2A@\3\2\2\2AB\3\2\2\2BH\3\2\2\2CD\6\3"+
		"\7\2DH\7\7\2\2EF\6\3\b\2FH\7\7\2\2G\25\3\2\2\2G!\3\2\2\2G*\3\2\2\2G\62"+
		"\3\2\2\2G9\3\2\2\2GC\3\2\2\2GE\3\2\2\2H\5\3\2\2\2IJ\5\b\5\2JK\7\3\2\2"+
		"KL\7\7\2\2L\7\3\2\2\2MN\7\7\2\2NP\7\4\2\2OM\3\2\2\2OP\3\2\2\2PQ\3\2\2"+
		"\2QR\7\7\2\2R\t\3\2\2\2\13\16\32\37&/\67AGO";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}