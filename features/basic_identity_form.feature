Feature: Basic Identify Form
  As a logged in user, I want to be able to create new cases. So that I should fill in Basic Identify Form.

  Background:
    Given I login RapidReg with "primero" account
#    Given I press "login" button
    And I press menu tab "Cases"


  Scenario: As a logged in user, I create a case by entering something in every field in the basic identity form
    When I press "add" button
    And I switch to full form
    And I scroll to "Basic Identity" form
    And I fill in the following:
      | Case Status                              | <Select> Open                                        |
      | Full Name                                | Tom Justin Clinton                                   |
      | First Name                               | Tom                                                  |
      | Middle Name                              | Justin                                               |
      | Surname                                  | Clinton                                              |
      | Nickname                                 | Tommy                                                |
      | Other Name                               | Tommy                                                |
      | Name(s) given to child after separation? | <Radio> No                                           |
      | Date of Registration or Interview        | <Date> Today's date                                  |
      | Sex                                      | <Radio> Male                                         |
      | Age                                      | 10                                                   |
      | Date of Birth                            | <Date> Today's date                                  |
      | Is the age estimated?                    | <Checkbox> Yes                                       |
      | Distinguishing Physical Characteristics  | Really tall, dark hair, brown eyes            |
      | Ration Card Number                       | 121121                                               |
      | ICRC Ref No.                             | 131313                                               |
      | RC ID No.                                | 141414                                               |
      | UNHCR ID                                 | AAA000                                               |
      | UN Number                                | EEE444                                               |
      | Other Agency ID                          | ABC12345                                             |
      | Other Agency Name                        | Test Agency                                          |
      | List of documents carried by the child   | Driver's License, Passport, Birth Certificate |
      | Current Civil/Marital Status             | <Select> Single                                      |
      | Occupation                               | Farmer                                               |
      | Current Address                          | 111 Main St, Davidson NC, 28036               |
      | Landmark                                 | A huge mountain                                      |
      | Is this address permanent?               | <Checkbox> Yes                                       |
      | Current Telephone                        | 336-555-1313                                         |
    And I press "save" button
    And I scroll to "Basic Identity" form
    Then I should see following:
      | Case Status                              | Open                                          |
      | Full Name                                | Tom Justin Clinton                            |
      | First Name                               | Tom                                           |
      | Middle Name                              | Justin                                        |
      | Surname                                  | Clinton                                       |
      | Nickname                                 | Tommy                                         |
      | Other Name                               | Tommy                                         |
      | Name(s) given to child after separation? | <Radio> No                                    |
      | Sex                                      | <Radio> Male                                  |
      | Age                                      | 10                                            |
      | Is the age estimated?                    | <Checkbox> Yes                                |
      | Distinguishing Physical Characteristics  | Really tall, dark hair, brown eyes            |
      | Ration Card Number                       | 121121                                        |
      | ICRC Ref No.                             | 131313                                        |
      | RC ID No.                                | 141414                                        |
      | UNHCR ID                                 | AAA000                                        |
      | UN Number                                | EEE444                                        |
      | Other Agency ID                          | ABC12345                                      |
      | Other Agency Name                        | Test Agency                                   |
      | List of documents carried by the child   | Driver's License, Passport, Birth Certificate |
      | Current Civil/Marital Status             | Single                                        |
      | Occupation                               | Farmer                                        |
      | Current Address                          | 111 Main St, Davidson NC, 28036               |
      | Landmark                                 | A huge mountain                               |
      | Is this address permanent?               | <Checkbox> Yes                                |
      | Current Telephone                        | 336-555-1313                                  |