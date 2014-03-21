import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "carersroot"

version := "1.0"

scalaVersion := "2.10.1"

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

libraryDependencies +=  "org.cddcore" %% "website" % "1.8.5.13"

libraryDependencies +=  "com.novocode" % "junit-interface" % "0.10-M2" % "test"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.M0"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.0"

libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test"    
            
testFrameworks := Seq(TestFrameworks.JUnit, TestFrameworks.ScalaCheck, TestFrameworks.ScalaTest, TestFrameworks.Specs2)