import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Quiz extends JFrame
{
    ArrayList<String[]> question = new ArrayList<>();
    int curr = 0;
    int score = 0;
    JLabel questionLabel ;
    JRadioButton a, b, c ,d;
    ButtonGroup option ;
    JButton nextButton;

    public Quiz() {
        setTitle("Quiz Management System - Quiz");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        nextButton = new JButton("Next");


        nextButton.addActionListener(e -> nextQuestion());

        questionLabel = new JLabel();
        a = new JRadioButton();
        b = new JRadioButton();
        c = new JRadioButton();
        d = new JRadioButton();

        option = new ButtonGroup();
        option.add(a);
        option.add(b);
        option.add(c);
        option.add(d);
        loadQuestion();

        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        panel.add(questionLabel);
        panel.add(a);
        panel.add(b);
        panel.add(c);
        panel.add(d);
        panel.add(nextButton);

        add(panel);

        if (question.isEmpty()) {
            questionLabel.setText("No questions found.");
            nextButton.setEnabled(false);
        } else {
            showQuestion();
        }

        setVisible(true);
    }


    private void loadQuestion() {
        String sql = "SELECT question, option_a, option_b, option_c, " +
                "option_d, correct_answer FROM questions ORDER BY id";

        try (Connection con = DBConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                question.add(new String[]{
                        rs.getString("question"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_answer")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this, "Unable to load questions: " + e.getMessage()
            );
        }
    }

    private void showQuestion() {
        String[] q = question.get(curr);

        questionLabel.setText("Question " + (curr + 1) + ": " + q[0]);

        a.setText("A. " + q[1]);
        b.setText("B. " + q[2]);
        c.setText("C. " + q[3]);
        d.setText("D. " + q[4]);

        option.clearSelection();

        nextButton.setText(
                curr == question.size() - 1 ? "Submit" : "Next"
        );
    }

    private void nextQuestion() {
        if (!a.isSelected() && !b.isSelected()
                && !c.isSelected() && !d.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select an answer.");
            return;
        }

        String selected = "";

        if (a.isSelected()) selected = "A";
        else if (b.isSelected()) selected = "B";
        else if (c.isSelected()) selected = "C";
        else if (d.isSelected()) selected = "D";

        if (selected.equalsIgnoreCase(question.get(curr)[5])) {
            score++;
        }

        if (curr < question.size() - 1) {
            curr++;
            showQuestion();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Quiz completed! Score: " + score + " / " + question.size()
            );
            dispose();
        }
    }
}
