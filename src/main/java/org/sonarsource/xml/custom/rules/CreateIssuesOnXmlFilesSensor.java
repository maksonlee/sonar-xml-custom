package org.sonarsource.xml.custom.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CreateIssuesOnXmlFilesSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(CreateIssuesOnXmlFilesSensor.class);

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Add parsing error on broken XML files");
    descriptor.onlyOnLanguage("xml");
    descriptor.createIssuesForRuleRepositories(XmlRulesDefinition.REPOSITORY);
  }

  @Override
  public void execute(SensorContext context) {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      Handler handler = new Handler(context);

      FileSystem fs = context.fileSystem();
      Iterable<InputFile> xmlFiles = fs.inputFiles(fs.predicates().hasLanguage("xml"));
      for (InputFile xmlFile : xmlFiles) {
        handler.xmlFile = xmlFile;
        saxParser.parse(xmlFile.file(), handler);
      }
    } catch (Exception e) {
      LOG.warn("Cause: {}", e.toString());
    }
  }
}

class Handler extends DefaultHandler {
  SensorContext context;
  InputFile xmlFile;

  public Handler(SensorContext context) {
    this.context = context;
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    createViolation(e);
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    createViolation(e);
  }

  public void createViolation(SAXParseException e) {
    NewIssue newIssue = context.newIssue()
            .forRule(XmlRulesDefinition.RULE_X1);

    NewIssueLocation primaryLocation = newIssue.newLocation()
            .on(xmlFile)
            .at(xmlFile.selectLine(e.getLineNumber()))
            .message(e.getMessage());
    newIssue.at(primaryLocation);
    newIssue.save();
  }
}
