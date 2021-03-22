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
package org.bravetogether.mapstories.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haim Adrian
 * @since 22-Mar-21
 */
@RestController
public class HomeController {
   @GetMapping("/")
   public ResponseEntity<?> homePage() {
      return ResponseEntity.ok("<h1>Welcome to Map Stories server</h1>\n" +
            "You have to sign in using Map Stories application in order to access services.");
   }
}

