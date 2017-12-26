package org.ms.media.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:mediaserver-context.xml")
public class MediaControllerTest {

  @Autowired
  private MockMvc mockMvc;
  
  @Test
  public void testScan() throws Exception {
    this.mockMvc.perform(get("/media/scan"))
    .andExpect(status().isOk())
    .andDo(print());
  }
  
  @Test
  public void testSearch() throws Exception {
    this.mockMvc.perform(get("/media/search/jagjit"))
    .andExpect(status().isOk())
    .andDo(print());
  }
}
