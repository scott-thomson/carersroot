import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "carers"

version := "1.0"

scalaVersion := "2.10.1"

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

libraryDependencies +=  "org.cddcore" %% "website" % "1.8.5.13" 

libraryDependencies +=  "com.novocode" % "junit-interface" % "0.10-M2" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.0"  excludeAll( ExclusionRule(organization = "org.eclipse.jetty") )

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test" excludeAll( ExclusionRule(organization = "org.eclipse.jetty") )

libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.2.7"
            
ivyXML := 
<dependency org="org.eclipse.jetty.orbit" name="javax.servlet" rev="3.0.0.v201112011016">
<artifact name="javax.servlet" type="orbit" ext="jar"/>
</dependency>
            
            
