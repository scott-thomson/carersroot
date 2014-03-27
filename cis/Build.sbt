import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "CIS"

version := "1.0"

scalaVersion := "2.10.1"

EclipseKeys.withSource := true

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

libraryDependencies +=   "org.cddcore" %% "website" % "1.8.5.16"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "8.0.0.M0"