lazy val cinnamonAkkaHttpTracing = project
  .in(file("."))
  .aggregate(frontend, service, backend)

lazy val frontend = project
  .in(file("frontend"))
  .enablePlugins(Cinnamon)
  .settings(
    scalafmtOnCompile := true,
    scalaVersion := "2.12.3",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.4" force(),
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.9",
    libraryDependencies += Cinnamon.library.cinnamonOpenTracingZipkin,
    cinnamon in run := true,
    connectInput in run := true // we wait on stdin
  )

lazy val service = project
  .in(file("service"))
  .enablePlugins(Cinnamon)
  .settings(
    scalafmtOnCompile := true,
    scalaVersion := "2.12.3",
    libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.4" force(),
    libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.4",
    libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.9",
    libraryDependencies += Cinnamon.library.cinnamonOpenTracingZipkin,
    cinnamon in run := true,
    connectInput in run := true // we wait on stdin
  )

lazy val backend = project
  .in(file("backend"))
  .enablePlugins(Cinnamon)
  .settings(
    scalafmtOnCompile := true,
    scalaVersion := "2.12.3",
    libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.4",
    libraryDependencies += Cinnamon.library.cinnamonOpenTracingZipkin,
    cinnamon in run := true,
    connectInput in run := true // we wait on stdin
  )
