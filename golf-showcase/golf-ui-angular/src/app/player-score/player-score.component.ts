import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-player-score',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './player-score.component.html',
  styleUrl: './player-score.component.less'
})
export class PlayerScoreComponent {
  myForm: FormGroup;
  status = "";

  constructor(private apiService: ApiService) {
    // Create form controls and group
    this.myForm = new FormGroup({
      name: new FormControl('', Validators.required),
      hole: new FormControl('', [Validators.required]),
      score: new FormControl('', [Validators.required])
    });
  }

  onSubmit(myform: any) {
    console.log("onsubmit"+this.myForm.valid);
   // if (this.myForm.valid) {
      console.log('Form Submitted!', myform.value);
      this.apiService.postScore(myform.value).subscribe({
        next: (response) => {
          console.log("success posted");
          this.status = "Success";
        },
        error: (err) => {
          console.error(err);
          this.status = "Failed";
        }
      });
   // }
  }
}
