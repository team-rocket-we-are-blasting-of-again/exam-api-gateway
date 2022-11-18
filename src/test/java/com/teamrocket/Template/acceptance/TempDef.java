package com.teamrocket.Template.acceptance;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

public class TempDef {
    @Given("a bowling game")
    public void aBowlingGame() {
    }

    @When("i roll {int} times and hit {int} each time")
    public void iRollRollsTimesAndHitAmountEachTime(int rolls, int amount) {
    }

    @Then("my final score is {int}")
    public void myFinalScoreIsFinalScore(int finalScore) {
        assertTrue(true);
    }
}
