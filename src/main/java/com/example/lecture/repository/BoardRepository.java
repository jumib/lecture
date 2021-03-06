package com.example.lecture.repository;

import com.example.lecture.entity.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//DB 처리에 대한 어노테이션 (component), 디비 컨트롤
@Repository
// BoardDataAccessObject(BoardDAO)
public class BoardRepository {
    static final Logger log =
            LoggerFactory.getLogger(BoardRepository.class);

    @Autowired
    // JdbcTemplate은 DB쿼리를 생성하는데 활용한다.
    private JdbcTemplate jdbcTemplate;

    public void create(Board board) throws Exception {
        log.info("Repository create()");

        //insert는 데이터를 입력함
        //board는 create table로 만든 내용
        //보드에 있는 title, content, writer 에
        //특정 값 3개를 삽입하기 위해 ?, ?, ?를 셋팅 한 상태
        String query = "insert into board(" +
                "title, content, writer) values(?, ?, ?)";
        //아래 getter를 이용해서 ?부분들에 값을 채운다.
        //즉 ?가 2개면 getter도 2개
        jdbcTemplate.update(query, board.getTitle(),
                board.getContent(), board.getWriter());
    }

    public List<Board> list() throws Exception {
        log.info("Repository list()");

        //select는 값을 선택해오는 녀석
        // 앞에 5개는 보드에 있는 정보
        // 조건을 줄 때 where을 준다
        // order by는 정렬조건
        List<Board> results = jdbcTemplate.query(
                "select board_no, title, content, " +
                        "writer, reg_date from board " +
                        "where board_no > 0 order by " +
                        "board_no desc, reg_date desc",
                //RowMapper는 <>에 대한 행값을 뽑아오는 것
                new RowMapper<Board>() {
                    @Override
                    public Board mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Board board = new Board();
                        board.setBoardNo(rs.getInt("board_no"));
                        board.setTitle(rs.getString("title"));
                        board.setContent(rs.getString("content"));
                        board.setWriter(rs.getString("writer"));
                        board.setRegDate(rs.getDate("reg_date"));
                        return board;
                    }
                });

        return results;
    }

    public Board read(Integer boardNo) throws Exception{
        List<Board> results = jdbcTemplate.query(
                "select board_no, title, content, writer, " +
                        "reg_date from board where board_no = ?",
                new RowMapper<Board>() {
                    @Override
                    public Board mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Board board = new Board();
                        board.setBoardNo(rs.getInt("board_no"));
                        board.setTitle(rs.getString("title"));
                        board.setWriter(rs.getString("writer"));
                        board.setContent(rs.getString("content"));
                        board.setRegDate(rs.getDate("reg_date"));

                        return board;
                    }
                }, boardNo
        );
        return results.isEmpty() ? null : results.get(0);
    }

    public void remove(Integer boardNo) throws Exception{
        //DB 테이블 안에 있는 내용을 지울때는 delete를 사용한다.
        String query = "delete from board where board_no = ?";
        jdbcTemplate.update(query, boardNo); //위에 물음표로 보드넘버 넣는것
    }

    public void modify(Board board) throws Exception{
        //DB 테이블의내용을 갱신하는데 사용한다.
        String query= "update board set title = ? ,content = ?" + " where board_no = ?";
        jdbcTemplate.update(
                query, board.getTitle(), board.getContent(), board.getBoardNo()
        );
    }
}