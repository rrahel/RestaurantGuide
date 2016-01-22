package helpers

import models.User
import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test.FakeEnvironment
import net.codingwell.scalaguice.ScalaModule
import play.api.inject.guice.GuiceApplicationBuilder
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by cemirrah13 on 19.01.2016.
 */
trait RepositoryAwareContext {
  val identity = User(Some(1),"John","Doe","jd@test.com", "test","test")
  val admin = User(Some(2),"Rrahel","Cemi","jm@test.com", "test","test", Set("USER", "ADMINISTRATOR"))

  implicit lazy val environment = FakeEnvironment[User, JWTAuthenticator](
  Seq(identity.loginInfo -> identity, admin.loginInfo->admin))

  val memDB = Map(
    "slick.dbs.default.driver"->"slick.driver.H2Driver$",
    "slick.dbs.default.db.driver"->"org.h2.Driver",
    "slick.dbs.default.db.url"->"jdbc:h2:mem:play;MODE=PostgreSQL"
  )

  class FakeModule extends AbstractModule with ScalaModule {
    def configure() = {
      bind[Environment[User, JWTAuthenticator]].toInstance(environment)
    }
  }

  lazy val application = new GuiceApplicationBuilder()
    .overrides(new FakeModule)
    .configure(memDB)
    .build()


}
