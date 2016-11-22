package org.sonarsource.xml.custom.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

public class XmlRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY = "xml-custom";
  public static final String JAVA_LANGUAGE = "xml";
  public static final RuleKey RULE_X1 = RuleKey.of(REPOSITORY, "ParsingError");

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY, JAVA_LANGUAGE).setName("My Custom XML Analyzer");

    repository.createRule(RULE_X1.rule())
      .setName("XML parser failure")
      .setHtmlDescription("When the XML parser fails, it is possible to record the failure as a violation on the file. This way, not only it is possible to track the number\n" +
              "of files that do not parse but also to easily find out why they do not parse.");

    // don't forget to call done() to finalize the definition
    repository.done();
  }
}
