package org.cddcore.carers

import java.util.concurrent.atomic.AtomicInteger
import scala.xml.Elem
import scala.xml.XML
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.selenium.HtmlUnit
import net.atos.carers.web.endpoint.ValidateClaimServer
import org.scalatest.junit.JUnitRunner
import java.net.URL
import java.net.HttpURLConnection

object SmokeWebtest {
  val port = new AtomicInteger(8090)
}

@RunWith(classOf[JUnitRunner])
class SmokeWebtest extends FlatSpec with ShouldMatchers with HtmlUnit with BeforeAndAfterAll {
  import SmokeWebtest._
  val localPort = port.getAndIncrement()
  val host = s"http://localhost:$localPort/"
  val server = ValidateClaimServer(localPort)

  override def beforeAll {
    server.start
  }

  override def afterAll {
    server.stop
  }

  "The default port method" should "return 8090 if PORT isn't set" in {
    val port = System.getenv("PORT")
    val expected = if (port == null) 8090 else port.toInt
    ValidateClaimServer.defaultPort should equal(expected)
  }

  "Our Rubbishy Website" should "Display a form when it recieves a GET" in {
    go to (host + "index.html")
    pageTitle should be("Validate Claim")
  }

  it should "be able to set focus to the custxml textarea" in {
    click on name("custxml")
  }

  it should "be able to set focus to the claimDate textarea" in {
    click on name("claimDate")
  }

  it should "be able to submit claim XML and then see a timeline if no date specified" in {
    textArea("custxml").value = getClaimXML
    textField("claimDate").value = ""
    click on id("submit")
    val xml: Elem = XML.loadString(pageSource)
    assertDivWithIdExists(xml, "timeLine")
  }

  it should "be able to submit claim XML and then see a result if date specified" in {
    textArea("custxml").value = getClaimXML
    textField("claimDate").value = "2010-5-1"
    click on id("submit")
    val xml: Elem = XML.loadString(pageSource)
    assertDivWithIdExists(xml, "oneTime")
  }

  it should "be able to submit claim XML and then see a timeline in json format" in {
    textArea("custxml").value = getClaimXML
    textField("claimDate").value = ""
    click on id("submitjson")
    val source: String = pageSource
    assert(source.startsWith("[{\"startDate\": \"2010-05-06\""))
  }

  it should "throw an exception if submitted with an invalid claimDate value" in {
    go to (host + "index.html")
    click on name("claimDate")
    textField("claimDate").value = "not a date"
    click on id("submit")
  }

  it should "throw an exception if submitted with invalid xml" in {
    go to (host + "index.html")
    textArea("custxml").value = "not xml"
    click on id("submit")
  }

  it should "execute a query lots of times without crashing " in {
    for (i <- 1 to 5) {
      go to (host + "index.html")
      textArea("custxml").value = getClaimXML
      textField("claimDate").value = ""
      click on id("submit")
      val xml: Elem = XML.loadString(pageSource)
      assertDivWithIdExists(xml, "timeLine")
    }
  }

  def getClaimXML: String = {
    val xml: Elem =
      <ValidateClaim xsi:schemaLocation="http://www.autotdd.com/ca Conversation%20v2_1%202010-07-16.xsd" xmlns="http://www.autotdd.com/ca" xmlns:n1="http://www.autotdd.com/ca" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        <piid>String</piid>
        <newClaimantData>true</newClaimantData>
        <ClaimantData>
          <ClaimantNINO>CL800119A</ClaimantNINO>
          <ClaimantNameDetails>
            <PersonNameTitle>MR</PersonNameTitle>
            <PersonGivenName>BREAK</PersonGivenName>
            <PersonFamilyName>IN-CARE</PersonFamilyName>
            <PersonNameSuffix>BSC</PersonNameSuffix>
            <PersonRequestedName>BREAKY</PersonRequestedName>
          </ClaimantNameDetails>
          <ClaimantBirthDate>
            <PersonBirthDate>1958-02-10</PersonBirthDate>
            <VerificationLevel>Level 1</VerificationLevel>
          </ClaimantBirthDate>
          <ClaimantMaritalStatus>
            <MaritalStatus>Married</MaritalStatus>
            <VerificationLevel>Level 1</VerificationLevel>
          </ClaimantMaritalStatus>
          <ClaimantGenderAtRegistration>1 = Male</ClaimantGenderAtRegistration>
          <ClaimantGenderCurrent>1 = Male</ClaimantGenderCurrent>
          <ClaimantNationality>GB</ClaimantNationality>
          <ClaimantContactDetails>
            <PreferredLanguages>en</PreferredLanguages>
            <Email n1:EmailUse="work" n1:EmailPreferred="yes">
              <EmailAddress>'breaks@care.com'</EmailAddress>
            </Email>
          </ClaimantContactDetails>
          <ClaimantAddress>
            <Line1>1 BREAK</Line1>
            <Line2>IN-CARE</Line2>
            <PostCode>BC1 1AA</PostCode>
          </ClaimantAddress>
        </ClaimantData>
        <newClaimData>true</newClaimData>
        <ClaimData>
          <ClaimStartDate>2010-05-06</ClaimStartDate>
          <ClaimNinoKnown>yes</ClaimNinoKnown>
          <ClaimPrevious>no</ClaimPrevious>
          <ClaimOverseas>no</ClaimOverseas>
          <ClaimEUArea>yes</ClaimEUArea>
          <ClaimEUDependantChildren>no</ClaimEUDependantChildren>
          <ClaimAlwaysUK>yes</ClaimAlwaysUK>
          <ClaimCurrentResidentUK>yes</ClaimCurrentResidentUK>
          <ClaimPartnerExists>yes</ClaimPartnerExists>
          <ClaimPartnerExistsDate>1990-05-10</ClaimPartnerExistsDate>
          <ClaimRelationToCarer>Husband</ClaimRelationToCarer>
          <ClaimPaidCare>no</ClaimPaidCare>
          <ClaimRivalCarer>no</ClaimRivalCarer>
          <Claim35Hours>yes</Claim35Hours>
          <ClaimBreakInCare>yes</ClaimBreakInCare>
          <ClaimBreaks>
            <BreakInCare>
              <BICFromDate>2010-05-30</BICFromDate>
              <BICToDate>2010-06-13</BICToDate>
              <BICReason>Hospitalisation</BICReason>
              <BICType>Hospital</BICType>
            </BreakInCare>
            <BreakInCare>
              <BICFromDate>2010-06-21</BICFromDate>
              <BICToDate>2010-06-30</BICToDate>
              <BICReason>Hospitalisation</BICReason>
              <BICType>Hospital</BICType>
            </BreakInCare>
          </ClaimBreaks>
          <ClaimPrior35Hours>yes</ClaimPrior35Hours>
          <ClaimPrior35HoursDate>2010-05-17</ClaimPrior35HoursDate>
          <ClaimPriorBreakInCare>no</ClaimPriorBreakInCare>
          <ClaimDependantAwayFromHome>no</ClaimDependantAwayFromHome>
          <ClaimEducationFullTime>no</ClaimEducationFullTime>
          <ClaimEmployment>no</ClaimEmployment>
          <ClaimRentalIncome>no</ClaimRentalIncome>
          <ClaimPaidClass2NIC>no</ClaimPaidClass2NIC>
          <ClaimSelfEmployed>no</ClaimSelfEmployed>
        </ClaimData>
        <newPartnerData>true</newPartnerData>
        <PartnerData>
          <PartnerNINO>DP800119A</PartnerNINO>
          <PartnerNameDetails>
            <PersonNameTitle>MRS</PersonNameTitle>
            <PersonGivenName>MANDY</PersonGivenName>
            <PersonFamilyName>IN-CARE</PersonFamilyName>
          </PartnerNameDetails>
          <PartnerBirthDate>
            <PersonBirthDate>1974-12-01</PersonBirthDate>
            <VerificationLevel>Level 1</VerificationLevel>
          </PartnerBirthDate>
          <PartnerMaritalStatus>
            <MaritalStatus>Married</MaritalStatus>
            <VerificationLevel>Level 1</VerificationLevel>
          </PartnerMaritalStatus>
          <PartnerGenderAtRegistration>2 = Female</PartnerGenderAtRegistration>
          <PartnerGenderCurrent>2 = Female</PartnerGenderCurrent>
          <PartnerNationality>GB</PartnerNationality>
          <PartnerContactDetails>
            <PreferredLanguages>en</PreferredLanguages>
          </PartnerContactDetails>
          <PartnerAddress>
            <Line1>1 BREAK</Line1>
            <Line2>IN-CARE</Line2>
            <PostCode>BC1 1AA</PostCode>
          </PartnerAddress>
        </PartnerData>
        <newDependantData>true</newDependantData>
        <DependantData>
          <DependantNINO>DP800119A</DependantNINO>
          <DependantNameDetails>
            <PersonNameTitle>MRS</PersonNameTitle>
            <PersonGivenName>MANDY</PersonGivenName>
            <PersonFamilyName>IN-CARE</PersonFamilyName>
          </DependantNameDetails>
          <DependantBirthDate>
            <PersonBirthDate>1974-12-01</PersonBirthDate>
            <VerificationLevel>Level 1</VerificationLevel>
          </DependantBirthDate>
          <DependantMaritalStatus>
            <MaritalStatus>Married</MaritalStatus>
            <VerificationLevel>Level 1</VerificationLevel>
          </DependantMaritalStatus>
          <DependantGenderAtRegistration>2 = Female</DependantGenderAtRegistration>
          <DependantGenderCurrent>2 = Female</DependantGenderCurrent>
          <DependantNationality>GB</DependantNationality>
          <DependantContactDetails>
            <PreferredLanguages>en</PreferredLanguages>
          </DependantContactDetails>
          <DependantAddress>
            <Line1>1 BREAK</Line1>
            <Line2>IN-CARE</Line2>
            <PostCode>BC1 1AA</PostCode>
          </DependantAddress>
        </DependantData>
        <newPaidCareData>false</newPaidCareData>
        <newRivalCarerData>false</newRivalCarerData>
        <newStatementData>true</newStatementData>
        <StatementData>
          <StatementType>SignedBySelf</StatementType>
          <StatementRole>Self</StatementRole>
          <StatementSignature>X</StatementSignature>
          <StatementDate>2010-06-28</StatementDate>
          <StatementHoursConfirmed>yes</StatementHoursConfirmed>
          <StatementHoursUnconfirmed>no</StatementHoursUnconfirmed>
          <StatementReason/>
        </StatementData>
        <newResidenceData>false</newResidenceData>
        <newEducationData>false</newEducationData>
        <newEmploymentData>false</newEmploymentData>
        <newExpensesData>false</newExpensesData>
        <newSelfEmpData>false</newSelfEmpData>
        <newOtherMoneyData>false</newOtherMoneyData>
        <newPaymentData>true</newPaymentData>
        <PaymentData>
          <PaymentPeriodicity>Weekly</PaymentPeriodicity>
          <PaymentAccountHolder>MR B IN-CARE</PaymentAccountHolder>
          <PaymentBankName>MIDLAND BANK</PaymentBankName>
          <PaymentSortCode>212121</PaymentSortCode>
          <PaymentAccountNumber>12345678</PaymentAccountNumber>
          <PaymentAlignAccount>yes</PaymentAlignAccount>
        </PaymentData>
        <newConsentData>true</newConsentData>
        <ConsentData>
          <ConsentAgreeEmployer>no</ConsentAgreeEmployer>
          <ConsentAgreeOthers>yes</ConsentAgreeOthers>
          <ConsentSignature>GOT ONE</ConsentSignature>
          <ConsentDate>2010-06-28</ConsentDate>
        </ConsentData>
      </ValidateClaim>

    xml.toString
  }
  def assertDivWithIdExists(xml: Elem, attributeId: String) = {
    val timeLineNodes = xml \\ "div"
    assert(timeLineNodes.length > 0)
    val optDivWithId = timeLineNodes.find((n) => {
      val a = n.attribute("id")
      a.isDefined && a.get.text == attributeId
    })
    assert(optDivWithId.isDefined)
  }
}
