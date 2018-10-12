package micronaut.upload.test;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.reactivex.Single;
import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;

@Controller("/upload")
public class UploadController {

    @Post(value = "streaming", consumes = MediaType.MULTIPART_FORM_DATA)
    public Single<HttpResponse<String>> streaming(StreamingFileUpload file) throws IOException {
        final File tempFile = new File("upload-" + file.getFilename());
        Publisher<Boolean> uploadPublisher = file.transferTo(tempFile);
        return Single.fromPublisher(uploadPublisher)
                .map(success -> {
                    System.out.println("Uploaded " + tempFile);
                    tempFile.delete();
                    if (success) {
                        return HttpResponse.ok("Uploaded");
                    } else {
                        return HttpResponse.<String>status(HttpStatus.CONFLICT)
                                .body("Upload Failed");
                    }
                });
    }

    @Post(value = "completed", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> completed(CompletedFileUpload file) {
        System.out.println("Uploaded" + file.getFilename());
        return HttpResponse.ok("Uploaded");
    }
}