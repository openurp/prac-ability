import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.prac.ability"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/prac-ability"),
    "scm:git@github.com:openurp/prac-ability.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Prac Ability"
ThisBuild / homepage := Some(url("http://openurp.github.io/prac-ability/index.html"))

val apiVer = "0.41.6"
val starterVer = "0.3.40"
val baseVer = "0.4.38"
val eduCoreVer = "0.3.0"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin, UndertowPlugin)
  .settings(
    name := "openurp-prac-ability-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_stater_web, openurp_base_tag, openurp_edu_core),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  )
