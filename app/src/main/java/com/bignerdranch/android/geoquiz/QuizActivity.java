package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String QUESTIONS_ANSWERED_KEY = "answered";
    private static final String TOTAL_ANSWERED = "total";
    private static final String TOTAL_CORRECT = "correct";
    private static final String IS_CHEATER = "cheater";
    private static final String CHEAT_COUNTER = "cheat_counter";
    private static final int REQUEST_CODE_CHEAT = 0;


    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private int mTotalAnswered = 0;
    private int mTotalCorrect = 0;
    private int mCheatCounter = 0;
    private boolean[] mIsCheater = new boolean[mQuestionBank.length];
    private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];

    public QuizActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mTotalAnswered = savedInstanceState.getInt(TOTAL_ANSWERED);
            mTotalCorrect = savedInstanceState.getInt(TOTAL_CORRECT);
            mQuestionsAnswered = savedInstanceState.getBooleanArray(QUESTIONS_ANSWERED_KEY);
            mIsCheater = savedInstanceState.getBooleanArray(IS_CHEATER);
            mCheatCounter = savedInstanceState.getInt(CHEAT_COUNTER);
        }

        TextView cheatCounter = (TextView) findViewById(R.id.cheat_counter);
        cheatCounter.setText((String) "Cheats Available " + (3 - mCheatCounter));

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Toast toast = Toast.makeText(QuizActivity.this,
//                                           R.string.correct_toast,
//                                           Toast.LENGTH_SHORT);
//              If you would like to position the toast at the top of the screen.
//              toast.setGravity(Gravity.TOP, 0 , 200);
//              toast.show();
                checkAnswer(true);

            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Toast toast = Toast.makeText(QuizActivity.this,
//                                           R.string.incorrect_toast,
//                                           Toast.LENGTH_SHORT);
//              toast.show();
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                // startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1 ) % mQuestionBank.length;
                // mIsCheater = false;
                updateQuestion();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                }
                // mIsCheater = false;
                updateQuestion();
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_CODE_CHEAT) {
            if(data == null) {
                return;
            }
            mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            mCheatCounter = mCheatCounter + 1;

            TextView cheatCounter = (TextView) findViewById(R.id.cheat_counter);
            cheatCounter.setText((String) "Cheats Available " + (3 - mCheatCounter));

            if(mCheatCounter == 3) {
                mCheatButton.setEnabled(false);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(TOTAL_ANSWERED, mTotalAnswered);
        savedInstanceState.putInt(TOTAL_CORRECT, mTotalCorrect);
        savedInstanceState.putInt(CHEAT_COUNTER, mCheatCounter);
        savedInstanceState.putBooleanArray(QUESTIONS_ANSWERED_KEY, mQuestionsAnswered);
        savedInstanceState.putBooleanArray(IS_CHEATER, mIsCheater);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);

        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        mQuestionsAnswered[mCurrentIndex] = true;
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);

        mTotalAnswered = mTotalAnswered + 1;

        int messageResId = 0;

        if(mIsCheater[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                mTotalCorrect = mTotalCorrect + 1;
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        if(mTotalAnswered == mQuestionBank.length) {
            Log.d(TAG, String.format("Value of mTotalCorrect: %d", mTotalCorrect));
            int tempGrade = (int) (((double) mTotalCorrect / (double) mTotalAnswered) * 100);
            Log.d(TAG, String.format("Value of tempGrade: %d", tempGrade) + "%");

            String string = String.format(Locale.US, "Correct: %d", tempGrade) + "%";
            Toast.makeText(this, string, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        }
    }
}
