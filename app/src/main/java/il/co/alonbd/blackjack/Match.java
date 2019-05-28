package il.co.alonbd.blackjack;


import android.app.Activity;
import android.app.AlertDialog;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Match {
    FrameLayout root;
    TokensDisplay moneyTv;
    TokensDisplay betTv;
    View dialogView;
    SeekBar dialogSeekBar;
    TextView dialogTextView;
    Button dialogKeepPlaying;
    Button dialogFinish;
    Button dialogAccept;
    TextView dialogScore;
    AlertDialog alertDialog;
    EditText nameEt;
    LinearLayout gameRoot;
    Hand dealerHand;
    Hand turn;
    ArrayList<Hand> hands;
    boolean btnLock;
    Hand defHand;
    TextView playerValue;
    TextView dealerValue;
    boolean isDealerPlayed;

    public Match(FrameLayout root) {
        this.root = root;
    }

    public void startMatch() {
        moneyTv = root.findViewById(R.id.money_tv);
        betTv = root.findViewById(R.id.bet_tv);
        gameRoot = root.findViewById(R.id.game_root_ll);
        playerValue = root.findViewById(R.id.hand_value);
        dealerValue = root.findViewById(R.id.dealer_hand_value);
        dealerHand = root.findViewById(R.id.dealer_hand);
        defHand = root.findViewById(R.id.hand1);
        hands = new ArrayList<>();
        hands.add(defHand);
        turn = defHand;

        moneyTv.setAmount(100);
        betTv.setAmount(0);
        startGame();
    }

    private void startGame() {
        isDealerPlayed = false;//To show value only at the end
        Animator.handAnimation(dealerHand, false, null);
        Animator.handAnimation(turn, false, null);
        btnLock = false;
        betDialog();
    }

    private void finishGame() {

        if (isDealerPlayed) {
            if (dealerHand.getMaxValue() < turn.getMaxValue()) {
                if (turn.isBj())
                    moneyTv.setAmount((int) (moneyTv.getAmount() + betTv.getAmount() * 2.5));
                else
                    moneyTv.setAmount(moneyTv.getAmount() + betTv.getAmount() * 2);
                Animator.finishAnim(root, Animator.WIN, new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        finishFinishGame();
                    }
                });
            } else if (dealerHand.getMaxValue() > turn.getMaxValue()) {
                Animator.finishAnim(root, Animator.LOST, new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        finishFinishGame();
                    }
                });
            } else if (dealerHand.getMaxValue() == turn.getMaxValue()) {
                moneyTv.setAmount(moneyTv.getAmount() + betTv.getAmount());
                Animator.finishAnim(root, Animator.PUSH, new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        finishFinishGame();
                    }
                });
            }
        } else {//Anim was called
            if (turn.isBj())
                moneyTv.setAmount(moneyTv.getAmount() + betTv.getAmount() * 5 / 2);
            finishFinishGame();
        }
    }

    private void finishFinishGame() {
        betTv.setAmount(0);
        if (moneyTv.getAmount() <= 0) {
            Animator.finishAnim(root, Animator.OWM, new Animator.EndListener() {
                @Override
                public void onEnd() {
                    finishMatch();
                }
            });
        } else {
            restartGame();
        }
    }

    private void restartGame() {
        dialogView = ((Activity) root.getContext()).getLayoutInflater().inflate(R.layout.dialog_finish, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        alertDialog = builder.setView(dialogView).setCancelable(false).create();
        alertDialog.show();
        dialogKeepPlaying = dialogView.findViewById(R.id.keep_playing_btn);
        dialogFinish = dialogView.findViewById(R.id.quit_btn);

        dialogKeepPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                dealerValue.setText("?");
                playerValue.setText("?");
                dealerHand.resetHand(null);
                turn.resetHand(new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        startGame();
                    }
                });

            }
        });
        dialogFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finishMatch();
            }
        });

    }

    private void finishMatch() {
        if (moneyTv.getAmount() == 0) {
            goBack();
        }
        else{

            dialogView = ((Activity) root.getContext()).getLayoutInflater().inflate(R.layout.dialog_hall_entry, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
            alertDialog = builder.setView(dialogView).setCancelable(false).create();
            alertDialog.show();
            dialogAccept = dialogView.findViewById(R.id.accept_btn);
            dialogScore = dialogView.findViewById(R.id.score_tv);
            dialogScore.setText(dialogScore.getText().toString()+moneyTv.getAmount());
            nameEt = dialogView.findViewById(R.id.name_et);
            dialogAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    HallOfFame.addEntry(moneyTv.getAmount(),nameEt.getText().toString(),root.getContext());
                    goBack();
                }
            });
        }
    }

    private void goBack() {
        ((MainActivity) root.getContext()).menuSetup();
    }

    protected boolean checkTurnHand() {
        boolean matchEnded = true;
        if (turn.isLost()) {
            Animator.finishAnim(root, Animator.BURNED, new Animator.EndListener() {
                @Override
                public void onEnd() {
                    finishGame();
                }
            });
        } else if (turn.isTwentyOne()) {
            if (turn.isBj()) {
                Animator.finishAnim(root, Animator.BJ, new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        finishGame();
                    }
                });
            } else
                dealerTurn();
        } else {
            matchEnded = false;
        }
        return matchEnded;
    }

    protected void dealerTurn() {
        btnLock = true;
        isDealerPlayed = true;
        dealerHand.dealerPlay(dealerValue, new Animator.EndListener() {
            @Override
            public void onEnd() {
                finishGame();
            }
        });

    }

    public void hit() {
        if (btnLock) return;
        btnLock = true;
        turn.hitHand(new Animator.EndListener() {
            @Override
            public void onEnd() {
                btnLock = false;
                checkTurnHand();
            }
        });
    }

    protected void doubleup() {
        if (btnLock) return;
        btnLock = true;
        if (betTv.getAmount() <= moneyTv.getAmount()) {
            int bet = betTv.getAmount();
            betTv.setAmount(2 * bet);
            moneyTv.setAmount(moneyTv.getAmount() - bet);
            turn.hitHand(new Animator.EndListener() {
                @Override
                public void onEnd() {
                    btnLock = false;
                    if (!checkTurnHand()) end();
                }
            });
        } else
            Toast.makeText(root.getContext(), "That is not enough money for a doubleup!", Toast.LENGTH_LONG).show();
    }

    protected void end() {
        if (btnLock) return;
        dealerTurn();
    }

    private void betDialog() {
        dialogView = ((Activity) root.getContext()).getLayoutInflater().inflate(R.layout.initialbetdialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
        alertDialog = builder.setView(dialogView).setCancelable(false).create();
        alertDialog.show();
        dialogSeekBar = dialogView.findViewById(R.id.bet_sb);
        dialogTextView = dialogView.findViewById(R.id.amount_tv);
        dialogSeekBar.setMax(101);
        dialogSeekBar.setProgress(50);
        combineSbAndTv();

        dialogView.findViewById(R.id.accept_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                betTv.setAmount(combineSbAndTv());
                moneyTv.setAmount(moneyTv.getAmount() - betTv.getAmount());
                alertDialog.dismiss();
                dealerHand.revealAll(true, dealerValue);
                turn.revealAll(false, playerValue);
                checkTurnHand();
            }
        });

        dialogSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                combineSbAndTv();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private int combineSbAndTv() {
        double percent = 0.009 * dialogSeekBar.getProgress() + 0.1;
        double betD = percent * moneyTv.getAmount();
        int bet = (int) betD;
        if (bet == 0) bet = 1;
        dialogTextView.setText(bet + "");
        return bet;
    }

}
