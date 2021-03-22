/*
 * Copyright 2021 Quest Software and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bravetogether.mapstories.server.config;

import org.bravetogether.mapstories.server.model.service.UserService;
import org.bravetogether.mapstories.server.security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SSL and JWT configuration
 * @author Haim Adrian
 * @since 22-Mar-21
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
   @Autowired
   private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

   @Autowired
   private JwtAuthenticationFilter jwtAuthenticationFilter;

   @Autowired
   private UserService userService;

   @Autowired
   private PasswordEncoder passwordEncoder;

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      // configure AuthenticationManager so that it knows from where to load user for matching credentials.
      // Use BCryptPasswordEncoder
      auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
   }

   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Override
   protected void configure(HttpSecurity httpSecurity) throws Exception {
      httpSecurity.csrf().disable().cors()
            .and()
            .authorizeRequests().antMatchers("/user/signin", "/user/signup", "/").permitAll() // Do not authenticate these requests
            .anyRequest().authenticated() // All other requests need to be authenticated
            .and()
            .requiresChannel().anyRequest().requiresSecure()
            .and()
            // make sure we use stateless session; session won't be used to store user's state.
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

      // Add a filter to validate the tokens with every request
      httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
   }
}

