package com.example.hangmann;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText edInput;
    TextView txtLettersTired;
    String lettersTired;
    final String MESSAGE_WITH_LETTERS_TIRED="Letters tired";
    TextView txtTriesLeft;
    String triesLeft;
    final String WINNING_MESSAGE="YOU WON !";
    final String LOSSING_MESSAGE="YOU LOST !";
    Animation rotateAnimation;
    Animation scaleAnimation;
    Animation scaleAndRotateAnimation;
    TableRow trReset;
    TableRow trTriesLeft;
    Button btnReset;


    void revealLetterInWord(char letter){
       int indexOfLetter=wordToBeGuessed.indexOf(letter);
        while (indexOfLetter>=0){
            wordDisplayedCharArray[indexOfLetter]=wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter=wordToBeGuessed.indexOf(letter,indexOfLetter+1);
        }
        wordDisplayedString=String.valueOf(wordDisplayedCharArray);

   }
   void displayWordOnScreen(){
      String formattedString="";
      for(char character:wordDisplayedCharArray){
          formattedString+=character+" ";
      }
      txtWordToBeGuessed.setText(formattedString);
   }
   void initializeGame() {

       Collections.shuffle(myListOfWords);  //randomly shuffling words in database
       wordToBeGuessed = myListOfWords.get(0);
       myListOfWords.remove(0);

       wordDisplayedCharArray = wordToBeGuessed.toCharArray();
       for(int i=1;i<wordDisplayedCharArray.length-1;i++) {
           wordDisplayedCharArray[i] = '_';
       }

           revealLetterInWord(wordDisplayedCharArray[0]);

           revealLetterInWord(wordDisplayedCharArray[wordDisplayedCharArray.length-1]);

           wordDisplayedString=String.valueOf(wordDisplayedCharArray);

           displayWordOnScreen();

           edInput.setText("");

           lettersTired=" ";
           txtLettersTired.setText(MESSAGE_WITH_LETTERS_TIRED);

           triesLeft=" X X X X X";
           txtTriesLeft.setText(triesLeft);



   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myListOfWords=new ArrayList<String>();
        txtWordToBeGuessed=findViewById(R.id.guessWord);
        edInput=findViewById(R.id.etinput);
        txtLettersTired=findViewById(R.id.lettersToBeTired);
        txtTriesLeft=findViewById(R.id.txtTriesLeft);
        rotateAnimation= AnimationUtils.loadAnimation(this,R.anim.rotate);
        scaleAnimation=AnimationUtils.loadAnimation(this,R.anim.scale);
        scaleAndRotateAnimation=AnimationUtils.loadAnimation(this,R.anim.scale_and_rotate);
        scaleAndRotateAnimation.setFillAfter(true);
        trReset=(TableRow) findViewById(R.id.trReset);
        trTriesLeft=(TableRow) findViewById(R.id.trTriesLeft);
        btnReset=findViewById(R.id.btnReset);


        //transvers database file and populate array list

        InputStream myInputStream=null;
        Scanner in=null;
        String aWord="";
        try {
            myInputStream = getAssets().open("database_file.txt");
            in=new Scanner(myInputStream);
            while (in.hasNext()){
                aWord=in.next();
                myListOfWords.add(aWord);


            }
        }
        catch (IOException e)
        {
            Toast.makeText(this,
                    e.getClass().getSimpleName()+":"+ e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        finally {
            if(in!=null) {
                in.close();
            }
            try {
                if( myInputStream != null) {
                    myInputStream.close();
                }
            } catch (IOException e) {
                Toast.makeText(this,
                        e.getClass().getSimpleName()+":"+ e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        initializeGame();

            //setup  the text changed listener for the edit text
        edInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if there is some leter on the input field
                if(s.length()!=0){
                    checkIfLetterIsInWord(s.charAt(0));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        }

        void checkIfLetterIsInWord(char letter){
       //if the letter was ound inside the word to be guesses
            if(wordToBeGuessed.indexOf(letter)>=0){
                //if the letter was displayed yet

                if(wordDisplayedString.indexOf(letter)<0){

                    //animate

                    txtWordToBeGuessed.startAnimation(scaleAnimation);

                    //replace the underscores with that letter
                    revealLetterInWord(letter);

                    //update the changes on screen
                    displayWordOnScreen();

                    //check if the game is won

                    if(!wordDisplayedString.contains("_")){
                        trTriesLeft.startAnimation(scaleAndRotateAnimation);
                      txtTriesLeft.setText(WINNING_MESSAGE);
                    }
                }
            }
            //otherwise if te letter was not found inside the word to be guessed
            else {
                //decreasing the number of tries left ,anf we'll show it on screen
                decreaseAndDisplayTriesLeft();
                //check if th game is lost
                if(triesLeft.isEmpty()){
                    trTriesLeft.startAnimation(scaleAndRotateAnimation);
                    txtTriesLeft.setText(LOSSING_MESSAGE);
                    txtWordToBeGuessed.setText(wordToBeGuessed);
                }

            }
            //display the letter that was tried

            if(lettersTired.indexOf(letter)<0){
                lettersTired +=letter+", ";
                String messageToBeDisplayed=MESSAGE_WITH_LETTERS_TIRED + lettersTired;
                txtLettersTired.setText(messageToBeDisplayed);
            }
        }
        void decreaseAndDisplayTriesLeft(){
       if(!triesLeft.isEmpty()){
           //animate
           txtTriesLeft.startAnimation(scaleAnimation);
           //take out the last 2 characters from this string
           triesLeft=triesLeft.substring(0,triesLeft.length()-2);
           txtTriesLeft.setText(triesLeft);
       }
        }





    public void resetGaming(View view) {
        trReset.startAnimation(rotateAnimation);
        //  clear animation
        trTriesLeft.clearAnimation();

        // set up a new Game

        initializeGame();
    }
}

