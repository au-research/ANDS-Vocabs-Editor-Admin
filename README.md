# ANDS-Vocabs-Editor-Admin

Web application for administering vocabularies stored in the ANDS
Vocabulary Editor.

# Installation

## Download

    git clone https://github.com/au-research/ANDS-Vocabs-Editor-Admin.git

## Configuration

    cd ANDS-Vocabs-Editor-Admin
    cd conf
    cp editoradmin.properties.sample editoradmin.properties
    cp requests.xml.sample requests.xml

Edit `editoradmin.properties` and `requests.xml` to suit your needs.

In `editoradmin.properties`, you need to provide two settings:

1. Set `PoolParty.remoteUrl` to the URL of the top level of the API of
   your PoolParty instance. The URL should end with `/PoolParty/`. The
   URL will look something like `https://my.poolparty.server/PoolParty/`
2. Set SPARQLResults.xsl to the filename of the XSL script you want to
   use to convert SPARQL Results into HTML. An example is provided in
   the conf directory. So the easiest way to proceed is to make this
   setting:

   ```SPARQLResults.xsl = SPARQLQueryResultstoXHTML.xsl```

The file `requests.xml` defines the queries and updates that will be
provided to users. The format of each request definition is straightforward:

    <!-- Put a comment here -->
    <request>
      <title><![CDATA[Put the label of the query/update here.
        Basic HTML tags such as <b>bold</b> can be used.]]></title>
      <type>Query</type> <!-- Either Query or Update -->
      <sparql><![CDATA[
      Put the text of the SPARQL Query/Update here.
      You can use template text such as #THESAURUS#, #THESAURUS/deprecated#,
      etc. to get the IRI of the named graph that is specific
      to the project being queried/updated.
    ]]></sparql>
    </request>

Once you've finished editing the configuration files:

    cd ..

## Build

    ant war

(We don't use Maven; we include all dependencies.)

## Deployment

We have been running this in a "plain" Tomcat 7 server, _not_ in a
full J2EE server (e.g., Wildfly). That's (one reason) why we include
all dependencies, including Jersey, Mojarra, and Weld.

If you try deploying in a J2EE server, it may not work.

To deploy, copy `vocabeditoradmin.war` into Tomcat's webapps directory.

If you wish, use Apache (or other web server) as a front-end proxy to Tomcat.

## Start using

Navigate to the front page, which (with the most basic installation
configuration) will be:

`http://server:8080/vocabeditoradmin/faces/login.xhtml`

Authentication uses the API of your PoolParty server. So
login using the username and password you use to log in to your
PoolParty server.
